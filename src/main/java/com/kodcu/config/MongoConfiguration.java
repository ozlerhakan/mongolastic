package com.kodcu.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

import static com.mongodb.assertions.Assertions.notNull;

/**
 * Created by Hakan on 5/19/2015.
 */
public class MongoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(MongoConfiguration.class);
    private final YamlConfiguration config;
    private MongoClient client;

    public MongoConfiguration(final YamlConfiguration config) {
        this.config = config;
        this.prepareClient();
    }

    private void prepareClient() {
        try {
            ServerAddress address = new ServerAddress(config.getMongo().getHost(), config.getMongo().getPort());
            MongoClientOptions options = MongoClientOptions.builder()
                    .serverSelectionTimeout(5000)
                    .socketKeepAlive(false)
                    .readPreference(ReadPreference.primaryPreferred())
                    .sslInvalidHostNameAllowed(true)
                    .build();

             client = connectToClient(address, options);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            System.exit(-1);
        }
    }

    private MongoClient connectToClient(ServerAddress address, MongoClientOptions options) {
        if (Objects.nonNull(config.getMongo().getAuth())) {

            String user = notNull("auth.name", config.getMongo().getAuth().getUser());
            String database = config.getMongo().getAuth().getSource();
            char[] pwd = config.getMongo().getAuth().getPwd().toCharArray();
            String mechanism = config.getMongo().getAuth().getMechanism();

            MongoCredential credential = findMongoCredential(user, database, pwd, mechanism);
            return new MongoClient(Arrays.asList(address), Arrays.asList(credential), options);

        } else {
            return new MongoClient(Arrays.asList(address), options);
        }
    }

    private MongoCredential findMongoCredential(String user, String database, char[] pwd, String mechanism) {
        MongoCredential credential = null;
        switch (mechanism) {
            case "scram-sha-1":
                credential = MongoCredential.createScramSha1Credential(user, database, pwd);
                break;
            case "x509":
                credential = MongoCredential.createMongoX509Credential(user);
                break;
            case "cr":
                credential = MongoCredential.createMongoCRCredential(user, database, pwd);
                break;
            case "plain":
                credential = MongoCredential.createPlainCredential(user, database, pwd);
                break;
            case "gssapi":
                credential = MongoCredential.createGSSAPICredential(user);
                break;
            default:
                credential = MongoCredential.createCredential(user, database, pwd);
                break;
        }
        return credential;
    }

    public MongoCollection<Document> getMongoCollection() {
        MongoCollection<Document> collection = null;
        try {
            MongoDatabase database = client.getDatabase(config.getMisc().getDindex().getName());
            collection = database.getCollection(config.getMisc().getCtype().getName());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return collection;
    }

    public void closeConnection() {
        if (Objects.nonNull(client))
            client.close();
    }

    public MongoClient getClient() {
        return client;
    }
}
