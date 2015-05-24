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
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Created by Hakan on 5/19/2015.
 */
public class Application {

    private static final Logger logger = Logger.getLogger(Application.class);
    private final String fileName;

    public Application(String file) {
        this.fileName = file;
    }

    public static void main(String[] args) throws Exception {
        configLog();
        configAssertion(args);
        String file = args[0];

        Application app = new Application(file);
        app.Start(args);
    }

    public static void configLog() throws IOException {
        PatternLayout patternLayout = new PatternLayout();
        FileAppender fileAppender = new FileAppender(patternLayout, "message.log", false);
        fileAppender.setThreshold(Level.DEBUG);
        BasicConfigurator.configure(fileAppender);
        BasicConfigurator.configure();
    }

    public void Start(String[] args) {
        FileConfiguration fConfig = new FileConfiguration(fileName);
        Optional<YamlConfiguration> yamlConfig = Optional.ofNullable(fConfig.getFileContent());

        if(args.length > 1)
            yamlConfig = Optional.ofNullable(checkConfigArguments(yamlConfig.get(), args));

        yamlConfig.ifPresent(config -> {
            MongoConfiguration mongo = new MongoConfiguration(config);
            JSONConverter converter = new JSONConverter(mongo.getMongoCollection());
            StringBuilder bulkJSONContent = converter.buildBulkJsonFile();
            converter.writeToFile(bulkJSONContent, config.getOutFile());
            mongo.closeConnection();
            logger.info("Cool! Your bulk JSON file generated successfully!");

            if (config.isEnableBulk()) {
                ElasticConfiguration client = new ElasticConfiguration(config);
                BulkService bulkService = new BulkService(config, client);
                bulkService.startBulkOperation();
            }
        });
    }

    static void configAssertion(String[] args) {

        if (args.length == 0) {
            logger.error("Incorrect syntax. Pass the name of the file");
            System.exit(-1);
        }
        if (!args[0].equals(Constants.CONFIG_FILE)) {
            logger.error("It is not a config.yml file we are looking for, Where is it?");
            System.exit(-1);
        }
    }

    public YamlConfiguration checkConfigArguments(YamlConfiguration config, String[] args){

        try {

            Class<?> cls = Class.forName("com.kodcu.config.YamlConfiguration");
            Method[] methlist = cls.getDeclaredMethods();

            int coupling = 0;
            String[] confParameter = null;

            for(int i=1; i < args.length; i++){
                confParameter = args[i].split(":");
                for (int j = 0; j < methlist.length; j++) {
                    Method m = methlist[j];

                    if(m.getName().equals(confParameter[0])) {
                        ++coupling;
                        Object value;
                        Class[] pvec = m.getParameterTypes();

                        if(pvec != null && pvec[0].toString().equals("boolean"))
                            value = Boolean.valueOf(confParameter[1]);
                        else
                            value = confParameter[1];

                        m.setAccessible(true);
                        m.invoke(config, value);
                    }
                }

                if(coupling == 0){
                    logger.error("Incorrect parameter. Pass the correct parameter name");
                    System.exit(-1);
                } else
                    coupling = 0;
            }

        } catch (Exception ex){
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        } finally {
            return config;
        }
    }
}
