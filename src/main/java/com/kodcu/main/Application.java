package com.kodcu.main;

import com.kodcu.config.FileConfiguration;
import com.kodcu.config.MongoConfiguration;
import com.kodcu.config.YamlConfiguration;
import com.kodcu.converter.JSONConverter;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Hakan on 5/19/2015.
 */
public class Application {
    private static final Logger logger = Logger.getLogger("Application");

    public static void main(String[] args) {

        if (args.length == 0) {
            logger.log(Level.SEVERE, "Specify the correct file");
            return;
        }
        String file = args[0];
        if (!file.equals("config.yml")) {
            logger.log(Level.SEVERE, "It is not a config.yml file we are looking for, Where is it?");
            return;
        }

        FileConfiguration fConfig = new FileConfiguration(file);
        Optional<YamlConfiguration> yamlConfig = Optional.ofNullable(fConfig.getFileContent());

        yamlConfig.ifPresent(config -> {
            MongoConfiguration mongo = new MongoConfiguration(config);
            JSONConverter converter = new JSONConverter(mongo.getMongoCollection());
            StringBuilder bulkJSONContent = converter.buildBulkJsonFile();
            converter.writeToFile(bulkJSONContent, config.getOutFile());
            mongo.closeConnection();
            System.out.println("Cool! Your bulk JSON file generated successfully!");
        });

    }
}
