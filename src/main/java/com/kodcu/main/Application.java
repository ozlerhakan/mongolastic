package com.kodcu.main;

import com.kodcu.client.ClientProvider;
import com.kodcu.config.FileConfiguration;
import com.kodcu.config.MongoConfiguration;
import com.kodcu.config.YamlConfiguration;
import com.kodcu.converter.JSONConverter;
import com.kodcu.util.Constants;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import java.io.*;
import java.util.Optional;
import java.util.logging.Level;
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

        /******************************************************************************************/
        //TODO iyilestirilmeli

        BulkRequestBuilder bulkRequest = ClientProvider.instance().getClient().prepareBulk();

        try (BufferedReader br = new BufferedReader(new FileReader(Constants.jsonOutputFileName))) {

            logger.log(Level.INFO, "Transferring data began to elasticsearch.");

            String line;
            String indexName  = null;
            String typeName   = null;
            String id         = null;

            while ((line = br.readLine()) != null) {

                if(line.contains("\"_index\":")){

                    indexName = line.substring(line.indexOf("\"_index\":") + 9, line.indexOf("\"_type\"")-1).replace("\"", "");
                    typeName  = line.substring(line.indexOf("\"_type\":") + 8, line.indexOf("\"_id\"")-1).replace("\"", "");
                    id        = line.substring(line.indexOf("\"_id\":") + 6, line.indexOf("}")-1).replace("\"", "");

                } else {

                    bulkRequest.add(ClientProvider.instance().getClient()
                            .prepareIndex(indexName, typeName, id)
                            .setSource(line));
                }
            }

            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if (bulkResponse.hasFailures()) {
                logger.log(Level.INFO, "Transfer failed.");
            }

            logger.log(Level.SEVERE, "Data transfer over.");

        } catch(IOException ex){

            //TODO exceptionlar dosyaya yazilmali
            logger.log(Level.SEVERE, "***********************************************");
            logger.log(Level.SEVERE, "Exception: " + ex.toString());
            logger.log(Level.SEVERE, "***********************************************");

        } finally {
            ClientProvider.instance().closeNode();
        }
        /******************************************************************************************/

    }

    static void configAssertion(String[] args) {
        if (args.length == 0) {
            logger.severe("Incorrect syntax. Pass the name of the file");
            System.exit(-1);
        }
        if (!args[0].equals(Constants.configFile)) {
            logger.severe("It is not a config.yml file we are looking for, Where is it?");
            System.exit(-1);
        }
    }
}
