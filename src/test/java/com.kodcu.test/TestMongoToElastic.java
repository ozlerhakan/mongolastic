package com.kodcu.test;

import com.kodcu.config.ElasticConfiguration;
import com.kodcu.config.FileConfiguration;
import com.kodcu.config.MongoConfiguration;
import com.kodcu.config.YamlConfiguration;
import com.kodcu.provider.ElasticToMongoProvider;
import com.kodcu.provider.MongoToElasticProvider;
import com.kodcu.provider.Provider;
import com.kodcu.service.BulkService;
import com.kodcu.service.ElasticBulkService;
import com.kodcu.service.MongoBulkService;
import com.kodcu.util.Log;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.IndicesAdminClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Hakan on 9/8/2015.
 */
@RunWith(Parameterized.class)
public class TestMongoToElastic {

    @Parameterized.Parameters(name = "{index}: ({0})={1}")
    public static Iterable<Object[]> queries() throws Exception {
        return Arrays.asList(new Object[][]{
                {new FileConfiguration(new String[]{"-f", "src/test/resources/conf1"})}
        });
    }

    private final FileConfiguration file;

    public TestMongoToElastic(final FileConfiguration file) {
        super();
        this.file = file;
    }

    @Test
    public void shouldCopyOneQueryToEsFromMongoDB() {
        Log.buildLog("TestMongoToElastic");

        YamlConfiguration config = file.getFileContent();
        assertThat(config, is(notNullValue()));

        if (Objects.isNull(System.getenv("MONGO_IP")))
            config.getMongo().setHost("localhost");
        else
            config.getMongo().setHost(System.getenv("MONGO_IP"));

        if (Objects.isNull(System.getenv("ES_IP")))
            config.getElastic().setHost("localhost");
        else
            config.getElastic().setHost(System.getenv("ES_IP"));


        ElasticConfiguration elastic = new ElasticConfiguration(config);
        MongoConfiguration mongo = new MongoConfiguration(config);

        BulkService bulkService = this.initializeBulkService(config, mongo, elastic);
        assertThat(bulkService, is(instanceOf(ElasticBulkService.class)));

        Provider provider = this.initializeProvider(config, mongo, elastic);
        assertThat(provider, is(instanceOf(MongoToElasticProvider.class)));

        provider.transfer(bulkService, config, () -> {
            bulkService.close();
            assertThat(provider.getCount(), equalTo(this.getCount(elastic, config)));
            elastic.closeNode();
            mongo.closeConnection();
        });
    }

    private Provider initializeProvider(YamlConfiguration config, MongoConfiguration mongo, ElasticConfiguration elastic) {
        if (config.getMisc().getDirection().equals("em")) {
            return new ElasticToMongoProvider(elastic, config);
        }
        return new MongoToElasticProvider(mongo.getMongoCollection(), config);
    }

    private BulkService initializeBulkService(YamlConfiguration config, MongoConfiguration mongo, ElasticConfiguration elastic) {
        if (config.getMisc().getDirection().equals("em")) {
            return new MongoBulkService(mongo.getClient(), config);
        }
        return new ElasticBulkService(config, elastic);
    }

    public long getCount(ElasticConfiguration elastic, YamlConfiguration config) {
        IndicesAdminClient admin = elastic.getClient().admin().indices();
        IndicesExistsRequestBuilder builder = admin.prepareExists(config.getMisc().getDindex().getAs());
        assertThat(builder.execute().actionGet().isExists(), is(true));

        elastic.getClient().admin().indices().flush(new FlushRequest(config.getMisc().getDindex().getAs())).actionGet();

        SearchResponse response = elastic.getClient().prepareSearch(config.getMisc().getDindex().getAs())
                .setTypes(config.getMisc().getCtype().getAs())
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setSize(0)
                .execute().actionGet();
        long count = response.getHits().getTotalHits();
        return count;
    }
}
