package com.kodcu.provider;

import com.kodcu.config.YamlConfiguration;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Hakan on 5/18/2015.
 */
public class MongoToElasticProvider implements Provider {

    private final Logger logger = Logger.getLogger(MongoToElasticProvider.class);
    private final MongoCollection<Document> collection;
    private final YamlConfiguration config;

    public MongoToElasticProvider(final MongoCollection<Document> collection, final YamlConfiguration config) {
        this.collection = collection;
        this.config = config;
    }

    @Override
    public long getCount() {
        long count = collection.count(Document.parse(config.getMongo().getQuery()));
        if (count == 0) {
            logger.info("Database/Collection does not exist or does not contain the record");
            System.exit(-1);
        }
        return count;
    }

    @Override
    public List buildJSONContent(int skip, int limit) {
        Document query = Document.parse(config.getMongo().getQuery());
        FindIterable<Document> results = collection.find(query).skip(skip).limit(limit);
        MongoCursor<Document> cursor = results.iterator();
        return Arrays.asList(cursor);
    }
}



