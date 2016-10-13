package com.kodcu.provider;

import com.kodcu.config.YamlConfiguration;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Hakan on 5/18/2015.
 */
public class MongoToElasticProvider implements Provider {

    private final Logger logger = Logger.getLogger(MongoToElasticProvider.class);
    private final MongoCollection<Document> collection;
    private final YamlConfiguration config;
    private MongoCursor<Document> cursor;
    private long cursorId = 0;

    public MongoToElasticProvider(final MongoCollection<Document> collection, final YamlConfiguration config) {
        this.collection = collection;
        this.config = config;
    }

    @Override
    public long getCount() {
        long count = collection.count(Document.parse(config.getMongo().getQuery()));
        logger.info("Mongo collection count: " + count);
        if (count == 0) {
            logger.error("Database/Collection does not exist or does not contain the record");
            System.exit(-1);
        }
        return count;
    }

    @Override
    public List buildJSONContent(int skip, int limit) {
        ArrayList<Document> result = new ArrayList<>(limit);
        result.ensureCapacity(limit);

        MongoCursor<Document> cursor = getCursor(skip);
        while(cursor.hasNext() && result.size() < limit) {
            result.add(cursor.next());
        }
        return result;
    }

    /**
     * Get the MongoDB cursor.
     */
    private MongoCursor<Document> getCursor(int skip) {
        if (cursor == null && cursorId == 0) {
            Document query = Document.parse(config.getMongo().getQuery());
            BasicDBObject sort = new BasicDBObject("$natural", 1);

            FindIterable<Document> results = collection
                .find(query)
                .sort(sort)
                .skip(skip)
                .noCursorTimeout(true);
            cursor = results.iterator();

            // TODO: Persist cursor ID somewhere to allow restarts.
            Optional.ofNullable(cursor.getServerCursor()).ifPresent(serverCursor -> cursorId = serverCursor.getId());
        } else if (cursor == null && cursorId != 0) {
            // TODO: Lookup cursor ID for resume.
            // Open existing cursor in case of restart??
        }

        return cursor;
    }
}
