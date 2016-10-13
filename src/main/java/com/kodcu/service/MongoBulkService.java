package com.kodcu.service;

import com.kodcu.config.YamlConfiguration;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.util.List;

/**
 * Created by Hakan on 6/29/2015.
 */
public class MongoBulkService implements BulkService {

    private final Logger logger = Logger.getLogger(MongoBulkService.class);
    private final MongoCollection<Document> collection;

    public MongoBulkService(final MongoClient client, final YamlConfiguration config) {
        this.collection = client.getDatabase(config.getMisc().getDindex().getAs()).getCollection(config.getMisc().getCtype().getAs());
    }

    @Override
    public void proceed(List content) {
        try {
            logger.info("Transferring data began to mongodb.");
            collection.insertMany((List<Document>) content);
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);
        }
    }

    @Override
    public void dropDataSet() {
        if (collection.count() != 0) {
            String collectionName = collection.getNamespace().getCollectionName();
            collection.drop();
            logger.info(String.format("The current collection called %s was deleted.", collectionName));
        }
    }

    @Override
    public void close() {
        //no-op
    }

}
