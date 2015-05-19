package com.kodcu.config;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Objects;

/**
 * Created by Hakan on 5/19/2015.
 */
public class MongoConfiguration {

    private final YamlConfiguration config;
    private MongoClient client;

    public MongoConfiguration(final YamlConfiguration config) {
        this.config = config;
    }

    public MongoCollection<Document> getMongoCollection() {
        ServerAddress address = new ServerAddress(config.getHost(), config.getPort());
        client = new MongoClient(address);
        MongoDatabase database = client.getDatabase(config.getDatabase());
        MongoCollection<Document> collection = database.getCollection(config.getCollection());
        return collection;
    }

    public void closeConnection() {
        if (Objects.nonNull(client))
            client.close();
    }
}
