package com.kodcu.main;

import com.kodcu.config.FileConfiguration;
import com.kodcu.config.MongoConfiguration;
import com.kodcu.config.YamlConfiguration;
import com.kodcu.converter.JSONConverter;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Created by Hakan on 5/19/2015.
 */
public class Application {
    private static final Logger logger = Logger.getLogger("Application");

    public static void main(String[] args) {

        configAssertion(args);
        String file = args[0];

        FileConfiguration fConfig = new FileConfiguration(file);
        Optional<YamlConfiguration> yamlConfig = Optional.ofNullable(fConfig.getFileContent());

        yamlConfig.ifPresent(config -> {
            MongoConfiguration mongo = new MongoConfiguration(config);
            JSONConverter converter = new JSONConverter(mongo.getMongoCollection());
            StringBuilder bulkJSONContent = converter.buildBulkJsonFile();
            converter.writeToFile(bulkJSONContent, config.getOutFile());
            mongo.closeConnection();
            logger.info("Cool! Your bulk JSON file generated successfully!");
        });

    }

    static void configAssertion(String[] args) {
        if (args.length == 0) {
            logger.severe("Incorrect syntax. Pass the name of the file");
            System.exit(-1);
        }
        if (!args[0].equals("config.yml")) {
            logger.severe("It is not a config.yml file we are looking for, Where is it?");
            System.exit(-1);
        }
    }
}
