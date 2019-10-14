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

    private final FileConfiguration file;

    public TestMongoToElastic(final FileConfiguration file) {
        super();
        this.file = file;
    }

    @Parameterized.Parameters(name = "{index}: ({0})={1}")
    public static Iterable<Object[]> queries() throws Exception {
        String query = "misc:\n" +
                "    dindex:\n" +
                "        name: twitter\n" +
                "    ctype:\n" +
                "        name: tweets\n" +
                "    batch: 500\n" +
                "mongo:\n" +
                "    host: mongo\n" +
                "    port: 27017\n" +
                "elastic:\n" +
                "    host: es\n" +
                "    port: 9300";
        return Arrays.asList(new Object[][]{
                {new FileConfiguration("src/test/resources/conf1")},
                {new FileConfiguration("src/test/resources/conf3")},
                {new FileConfiguration(query)}
        });
    }

    @Test
    public void shouldCopyOneQueryToEsFromMongoDB() {
        YamlConfiguration config = file.getFileContent();
        assertThat(config, is(notNullValue()));

        if (Objects.isNull(config.getMongo().getAuth()))
            if (Objects.nonNull(System.getenv("MONGO_IP")))
                config.getMongo().setHost(System.getenv("MONGO_IP"));
            else
                config.getMongo().setHost("localhost");
        else {
            if (Objects.nonNull(System.getenv("MONGO_AUTH_IP")))
                config.getMongo().setHost(System.getenv("MONGO_AUTH_IP"));
            else
                return;
        }

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
        long count = response.getHits().getTotalHits().value;
        return count;
    }
}
