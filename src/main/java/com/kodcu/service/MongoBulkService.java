package com.kodcu.service;

import com.kodcu.config.YamlConfiguration;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertOneModel;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hakan on 6/29/2015.
 */
public class MongoBulkService implements BulkService {

    private final Logger logger = Logger.getLogger(MongoBulkService.class);
    private final MongoCollection<Document> collection;

    public MongoBulkService(final MongoClient client, final YamlConfiguration config) {
        this.collection = client.getDatabase(config.getAsDatabase()).getCollection(config.getAsCollection());
    }

    @Override
    public void proceed(String jsonContent) {

        if(jsonContent.isEmpty()) {
            logger.debug("Index/Type does not exist or does not contain the record");
            return;
        }

        try {
            logger.info("Transferring data began to mongodb.");
            final List<InsertOneModel<Document>> list = new ArrayList<>();
            for (String json : jsonContent.split(System.lineSeparator())) {
                final byte bytes[] = json.getBytes(Charset.forName("UTF-8"));
                list.add(new InsertOneModel<>(Document.parse(new String(bytes, Charset.forName("UTF-8")))));
            }
            collection.bulkWrite(list);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
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
    }

}
