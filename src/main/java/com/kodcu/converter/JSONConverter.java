package com.kodcu.converter;

import com.mongodb.MongoNamespace;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Hakan on 5/18/2015.
 */
public class JSONConverter {

    private final Logger logger = Logger.getLogger("JSONConverter");
    private final MongoCollection<Document> collection;

    public JSONConverter(final MongoCollection<Document> collection) {
        this.collection = collection;
    }

    public StringBuilder buildBulkJsonFile() {
        FindIterable<Document> results = collection.find();
        MongoCursor<Document> cursor = results.iterator();
        StringBuilder sb = new StringBuilder();

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            Set<Map.Entry<String, Object>> set = doc.entrySet();

            JsonObjectBuilder jsonObj = Json.createObjectBuilder();

            set.stream().forEach(entry -> {
                if (entry.getKey().equals("_id")) {
                    MongoNamespace namespace = collection.getNamespace();
                    JsonObjectBuilder create = Json.createObjectBuilder();
                    create.add("create", Json.createObjectBuilder()
                            .add("_index", namespace.getDatabaseName())
                            .add("_type", namespace.getCollectionName())
                            .add("_id", entry.getValue().toString()));
                    sb.append(create.build().toString());
                    sb.append(System.lineSeparator());
                } else {
                    this.buildJson(jsonObj, entry, sb);
                }
            });

            sb.append(jsonObj.build().toString());
            sb.append(System.lineSeparator());
        }

        return sb;
    }

    private void buildJson(JsonObjectBuilder jsonObj, Map.Entry<String, Object> entry, StringBuilder sb) {
        Object value = entry.getValue();
        String field = entry.getKey();
        if (Objects.isNull(value)) {
            jsonObj.add(field, JsonValue.NULL);
        } else {
            if (value instanceof Document) {
                Document doc = (Document) value;
                Set<Map.Entry<String, Object>> set = doc.entrySet();
                JsonObjectBuilder sub = Json.createObjectBuilder();

                set.stream().forEach(subEntry -> {
                    this.buildJson(sub, subEntry, sb);
                });
                jsonObj.add(field, sub);
            } else if (value instanceof Boolean) {
                Boolean bool = (Boolean) value;
                jsonObj.add(field, bool.booleanValue());
            } else if (value instanceof Number) {
                Number number = (Number) value;
                jsonObj.add(field, number.doubleValue());
            } else if (value instanceof Date) {
                Date date = (Date) value;
                jsonObj.add(field, date.toString());
            } else if (value instanceof ArrayList) {
                ArrayList list = (ArrayList) value;
                JsonArrayBuilder sub = Json.createArrayBuilder();
                list.forEach(item -> {
                    this.buildJson(sub, item, sb);
                });
                jsonObj.add(field, sub);
            } else {
                jsonObj.add(field, value.toString());
            }
        }
    }


    private void buildJson(JsonArrayBuilder jsonArray, Object value, StringBuilder sb) {
        if (Objects.isNull(value)) {
            jsonArray.add(JsonValue.NULL);
        } else {
            if (value instanceof Document) {
                Document doc = (Document) value;
                Set<Map.Entry<String, Object>> set = doc.entrySet();
                JsonObjectBuilder subObj = Json.createObjectBuilder();
                set.stream().forEach(entry -> {
                    this.buildJson(subObj, entry, sb);
                });
                jsonArray.add(subObj);
            } else if (value instanceof Boolean) {
                Boolean bool = (Boolean) value;
                jsonArray.add(bool.booleanValue());
            } else if (value instanceof Number) {
                Number number = (Number) value;
                jsonArray.add(number.doubleValue());
            } else if (value instanceof Date) {
                Date date = (Date) value;
                jsonArray.add(date.toString());
            } else if (value instanceof ArrayList) {
                ArrayList list = (ArrayList) value;
                JsonArrayBuilder sub = Json.createArrayBuilder();
                list.forEach(item -> {
                    this.buildJson(sub, item, sb);
                });
                jsonArray.add(sub);
            } else {
                jsonArray.add(value.toString());
            }
        }
    }

    public void writeToFile(StringBuilder sb, String outFile) {
        try {
            File file = new File(outFile.concat(".json"));
            Files.write(file.toPath(), sb.toString().getBytes(Charset.forName("UTF-8")));
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }
}



