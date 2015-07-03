package com.kodcu.provider;

import com.kodcu.config.YamlConfiguration;
import com.kodcu.converter.JsonBuilder;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.apache.log4j.Logger;
import org.bson.Document;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Map;
import java.util.Set;

/**
 * Created by Hakan on 5/18/2015.
 */
public class MongoToElasticProvider extends Provider {

    private final Logger logger = Logger.getLogger(MongoToElasticProvider.class);
    private final MongoCollection<Document> collection;
    private final JsonBuilder builder;
    private final YamlConfiguration config;

    public MongoToElasticProvider(final MongoCollection<Document> collection, final YamlConfiguration config, final JsonBuilder builder) {
        this.collection = collection;
        this.config = config;
        this.builder = builder;
    }

    protected long getCount() {
        long count = collection.count(Document.parse(config.getMongoQuery()));
        if (count == 0) {
            logger.info("Database/Collection does not exist or does not contain the record");
            System.exit(-1);
        }
        return count;
    }

    @Override
    public String buildJSONContent(int skip, int limit) {
        Document query = Document.parse(config.getMongoQuery());
        FindIterable<Document> results = collection.find(query).skip(skip).limit(limit);
        MongoCursor<Document> cursor = results.iterator();
        StringBuilder sb = new StringBuilder();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            Set<Map.Entry<String, Object>> set = doc.entrySet();

            JsonObjectBuilder jsonObj = Json.createObjectBuilder();

            set.stream().forEach(entry -> {
                if (entry.getKey().equals("_id")) {
                    JsonObjectBuilder create = Json.createObjectBuilder();
                    create.add("create", Json.createObjectBuilder()
                            .add("_index", config.getAsDatabase())
                            .add("_type", config.getAsCollection())
                            .add("_id", entry.getValue().toString()));
                    sb.append(create.build().toString());
                    sb.append(System.lineSeparator());
                } else {
                    builder.buildJson(jsonObj, entry, sb);
                }
            });

            sb.append(jsonObj.build().toString());
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}



