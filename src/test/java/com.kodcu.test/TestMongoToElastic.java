package com.kodcu.test;

import com.kodcu.config.ElasticConfiguration;
import com.kodcu.config.FileConfiguration;
import com.kodcu.config.MongoConfiguration;
import com.kodcu.config.YamlConfiguration;
import com.kodcu.converter.JsonBuilder;
import com.kodcu.provider.ElasticToMongoProvider;
import com.kodcu.provider.MongoToElasticProvider;
import com.kodcu.provider.Provider;
import com.kodcu.service.BulkService;
import com.kodcu.service.ElasticBulkService;
import com.kodcu.service.MongoBulkService;
import com.kodcu.util.Log;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Hakan on 9/8/2015.
 */
public class TestMongoToElastic {

    @Test
    public void shouldCopyOneQueryToEsFromMongoDB() {
        Log.buildLog("TestMongoToElastic");
        FileConfiguration fConfig = new FileConfiguration(new String[]{"-f", "src/test/resources/conf1"});
        assertThat(fConfig.getFileContent(), is(notNullValue()));

        YamlConfiguration config = fConfig.getFileContent();
        ElasticConfiguration elastic = new ElasticConfiguration(config);
        MongoConfiguration mongo = new MongoConfiguration(config);

        BulkService bulkService = this.initializeBulkService(config, mongo, elastic);
        assertThat(bulkService, is(instanceOf(ElasticBulkService.class)));

        Provider provider = this.initializeProvider(config, mongo, elastic);
        assertThat(provider, is(instanceOf(MongoToElasticProvider.class)));
        assertThat(provider.getCount(), equalTo(1L));

        provider.transfer(bulkService, () -> {
            bulkService.close();
            mongo.closeConnection();
            elastic.closeNode();
        });
    }

    private Provider initializeProvider(YamlConfiguration config, MongoConfiguration mongo, ElasticConfiguration elastic) {
        if (config.getMisc().getDirection().equals("em")) {
            return new ElasticToMongoProvider(elastic, config, new JsonBuilder());
        }
        return new MongoToElasticProvider(mongo.getMongoCollection(), config, new JsonBuilder());
    }

    private BulkService initializeBulkService(YamlConfiguration config, MongoConfiguration mongo, ElasticConfiguration elastic) {
        if (config.getMisc().getDirection().equals("em")) {
            return new MongoBulkService(mongo.getClient(), config);
        }
        return new ElasticBulkService(config, elastic);
    }
}
