package com.kodcu.main;

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
import com.kodcu.util.Constants;
import org.apache.log4j.*;

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
        PatternLayout patternLayout = new PatternLayout("[%d{yyyy-MM-dd}] [%t] [%p]:%m%n");
        FileAppender fileAppender = new FileAppender(patternLayout, "mongolastic.log", false);
        fileAppender.setThreshold(Level.DEBUG);
        BasicConfigurator.configure(fileAppender);
        BasicConfigurator.configure();
    }

    public void start() {
        FileConfiguration fConfig = new FileConfiguration(args);
        Optional<YamlConfiguration> yamlConfig = Optional.ofNullable(fConfig.getFileContent());

        yamlConfig.ifPresent(config -> {
            if (config.isFromMongo()) {
                this.startFromMongo(config);
            } else {
                this.startFromElastic(config);
            }
        });
    }

    private void startFromElastic(YamlConfiguration config) {
        MongoConfiguration mongo = new MongoConfiguration(config);
        BulkService bulkService = new MongoBulkService(mongo.getClient(), config);
        ElasticConfiguration elastic = new ElasticConfiguration(config);
        Provider provider = new ElasticToMongoProvider(elastic, config, new JsonBuilder());
        provider.transfer(bulkService, () -> {
            mongo.closeConnection();
            elastic.closeNode();
        });
    }

    public void startFromMongo(YamlConfiguration config) {
        ElasticConfiguration elastic = new ElasticConfiguration(config);
        BulkService bulkService = new ElasticBulkService(config, elastic);
        MongoConfiguration mongo = new MongoConfiguration(config);
        Provider provider = new MongoToElasticProvider(mongo.getMongoCollection(), config, new JsonBuilder());
        provider.transfer(bulkService, () -> {
            bulkService.close();
            mongo.closeConnection();
            elastic.closeNode();
        });
    }

    static void configAssertion(String[] args) {
        if (args.length == 0) {
            logger.error("Incorrect syntax. Pass the correct parameter(s)");
            System.exit(-1);
        }
        if (!args[0].endsWith(Constants.MONGOLASTIC_FILE)) {
            logger.error("It is not a mongolastic file we are looking for, Where is it?");
            System.exit(-1);
        }
        if (args.length > 7) {
            logger.error("Incorrect syntax. Pass max 7 parameters");
            System.exit(-1);
        }
    }
}
