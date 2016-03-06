package com.kodcu.main;

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
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by Hakan on 5/19/2015.
 */
public class Application {

    private static final Logger logger = Logger.getLogger(Application.class);
    private final String[] args;

    public Application(String[] args) {
        this.args = args;
    }

    public static void main(String[] args) throws Exception {
        configLog();
        configAssertion(args);

        Application app = new Application(args);
        app.start();
    }

    public static void configLog() throws IOException {
        Log.buildLog();
    }

    public void start() {
        FileConfiguration fConfig = new FileConfiguration(args);
        Optional<YamlConfiguration> yamlConfig = Optional.ofNullable(fConfig.getFileContent());
        yamlConfig.ifPresent(this::proceedService);
    }

    public void proceedService(YamlConfiguration config) {
        ElasticConfiguration elastic = new ElasticConfiguration(config);
        MongoConfiguration mongo = new MongoConfiguration(config);
        BulkService bulkService = this.initializeBulkService(config, mongo, elastic);
        Provider provider = this.initializeProvider(config, mongo, elastic);
        provider.transfer(bulkService, config, () -> {
            bulkService.close();
            mongo.closeConnection();
            elastic.closeNode();
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

    static void configAssertion(String[] args) {
        if (args.length == 0) {
            logger.error("Incorrect syntax. Should be mongolastic.jar -f /path/yml/file");
            System.exit(-1);
        }
        if (!args[0].equals("-f")) {
            logger.error("Please specify the -f parameter with a correct yaml file");
            System.exit(-1);
        }
        if (args.length != 2) {
            logger.error("Incorrect syntax. Pass max 2 parameters");
            System.exit(-1);
        }
    }
}
