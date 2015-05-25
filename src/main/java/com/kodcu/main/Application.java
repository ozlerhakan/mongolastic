package com.kodcu.main;

import com.kodcu.config.ElasticConfiguration;
import com.kodcu.config.FileConfiguration;
import com.kodcu.config.MongoConfiguration;
import com.kodcu.config.YamlConfiguration;
import com.kodcu.converter.JSONConverter;
import com.kodcu.service.BulkService;
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
        app.Start();
    }

    public static void configLog() throws IOException {
        PatternLayout patternLayout = new PatternLayout();
        FileAppender fileAppender = new FileAppender(patternLayout, "mongolastic.log", false);
        fileAppender.setThreshold(Level.DEBUG);
        BasicConfigurator.configure(fileAppender);
        BasicConfigurator.configure();
    }

    public void Start() {
        FileConfiguration fConfig = new FileConfiguration(args);
        Optional<YamlConfiguration> yamlConfig = Optional.ofNullable(fConfig.getFileContent());

        yamlConfig.ifPresent(config -> {
            MongoConfiguration mongo = new MongoConfiguration(config);
            JSONConverter converter = new JSONConverter(mongo.getMongoCollection());
            StringBuilder bulkJSONContent = converter.buildBulkJsonFile();
            converter.writeToFile(bulkJSONContent, config.getOutFile(), () -> {
                logger.info("Cool! Your bulk JSON file generated successfully!");
            });
            mongo.closeConnection();

            if (config.isEnableBulk()) {
                ElasticConfiguration client = new ElasticConfiguration(config);
                BulkService bulkService = new BulkService(config, client);
                bulkService.startBulkOperation();
            }
        });
    }

    static void configAssertion(String[] args) {
        if (args.length == 0) {
            logger.error("Incorrect syntax. Pass the correct parameter(s)");
            System.exit(-1);
        }
        if (!args[0].equals(Constants.CONFIG_FILE)) {
            logger.error("It is not a config.yml file we are looking for, Where is it?");
            System.exit(-1);
        }
        if (args.length > 9) {
            logger.error("Incorrect syntax. Pass max 9 parameters");
            System.exit(-1);
        }
    }
}
