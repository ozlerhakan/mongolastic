package com.kodcu.converter;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Hakan on 6/30/2015.
 */
public class JsonBuilder {

    public void buildJson(JsonObjectBuilder jsonObj, Map.Entry<String, Object> entry, StringBuilder sb) {
        Object value = entry.getValue();
        String field = entry.getKey();
        if (Objects.isNull(value)) {
            jsonObj.add(field, JsonValue.NULL);
        } else {
            if (value instanceof Map) {
                Map doc = (Map) value;
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
                byte bytes[] = value.toString().getBytes(Charset.forName("UTF-8"));
                String encodedContent = new String(bytes, Charset.forName("UTF-8"));
                jsonObj.add(field, encodedContent);
            }
        }
    }


    private void buildJson(JsonArrayBuilder jsonArray, Object value, StringBuilder sb) {
        if (Objects.isNull(value)) {
            jsonArray.add(JsonValue.NULL);
        } else {
            if (value instanceof Map) {
                Map doc = (Map) value;
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
                byte bytes[] = value.toString().getBytes(Charset.forName("UTF-8"));
                String encodedContent = new String(bytes, Charset.forName("UTF-8"));
                jsonArray.add(encodedContent);
            }
        }
    }
}
