package com.kodcu.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Hakan on 5/19/2015.
 */
public class MongoConfiguration {

    private final Logger logger = Logger.getLogger(MongoConfiguration.class);
    private final YamlConfiguration config;
    private MongoClient client;

    public MongoConfiguration(final YamlConfiguration config) {
        this.config = config;
        this.prepareClient();
    }

    private void prepareClient() {
        try {
            ServerAddress address = new ServerAddress(config.getMongoHost(), config.getMongoPort());
            MongoClientOptions options = MongoClientOptions.builder()
                    .serverSelectionTimeout(5000)
                    .socketKeepAlive(false)
                    .readPreference(ReadPreference.primaryPreferred())
                    .build();
            client = new MongoClient(Arrays.asList(address), options);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
    }

    public MongoCollection<Document> getMongoCollection() {
        MongoCollection<Document> collection = null;
        try {
            MongoDatabase database = client.getDatabase(config.getDatabase());
            collection = database.getCollection(config.getCollection());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
        return collection;
    }

    public void closeConnection() {
        if (Objects.nonNull(client))
            client.close();
    }

    public MongoClient getClient(){
        return client;
    }
}
