package com.kodcu.util;

import com.kodcu.lang.QueryParser;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hakan on 6/28/2015.
 */
public class QueryWorker {

    private static final Logger logger = Logger.getLogger(QueryWorker.class);
    private Map<String, String> map;
    private List<String> properties;
    private String prefix;
    private boolean fromEs;
    private boolean fromMongo;

    public boolean isFromEs() {
        return fromEs;
    }

    public void setFromEs(boolean fromEs) {
        this.fromEs = fromEs;
    }

    public boolean isFromMongo() {
        return fromMongo;
    }

    public void setFromMongo(boolean fromMongo) {
        this.fromMongo = fromMongo;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<String> getProperties() {
        return properties;
    }

    public String getPropertiesAsString() {
        return String.join("\n", properties);
    }

    public void addProperty(String property) {
        this.getProperties().add(property);
    }

    public void setCollectionName(String name) {
        String collection = this.setKeyValue("collection", name);
        this.addProperty(collection);
        String newcollection = this.setKeyValue("asCollection", name);
        this.addProperty(newcollection);
    }

    public void setDatabaseName(String databaseName) {
        String db = this.setKeyValue("database", databaseName);
        this.addProperty(db);
        String newdb = this.setKeyValue("asDatabase", databaseName);
        this.addProperty(newdb);
    }

    public void mongoDeclaration(String where) {
        if (where.equalsIgnoreCase("from")) {
            String mongo = this.setKeyValue("fromMongo", "true");
            this.addProperty(mongo);
            this.setFromMongo(true);
        } else if (where.equalsIgnoreCase("to") && isFromMongo()) {
            logger.error("You cannot set two mongo instances in a query!");
            System.exit(-1);
        }
        this.setPrefix("mongo");
    }

    public void addKeyValue(QueryParser.PropertyContext ctx) {
        String value = this.setValue(ctx.key().getText(), ctx.value().getText());
        String key = String.join("",
                this.getPrefix(),
                this.getFirstLetterOfString(ctx),
                this.getSubString(ctx, 1));
        boolean duplicateProperty = map.containsKey(key);
        if (duplicateProperty) {
            logger.error(String.format("You cannot define the %s more than once in the %s configuration!", ctx.key().getText(), getPrefix()));
            System.exit(-1);
        }
        final String property = this.setKeyValue(key, value);
        this.addProperty(property);
    }

    private String setValue(String key, String value) {
        if (key.equalsIgnoreCase("query")) {
            final File configFile = new File(value.substring(1, value.length() - 1));
            if (configFile.isFile() && this.getPrefix().equals("mongo")) {
                logger.error("You can only give a json file path for the es configuration!");
                System.exit(-1);
            } else if (this.getPrefix().equals("es")) {
                try {
                    String query = FileUtils.readFileToString(configFile, "UTF-8");
                    query = query.replaceAll("\"", "\\\\\"");
                    value = "\"".concat(query).concat("\"");
                } catch (IOException e) {
                    logger.error(e.getMessage(), e.fillInStackTrace());
                    System.exit(-1);
                }
            }
        }
        return value;
    }

    private String getSubString(QueryParser.PropertyContext ctx, int startFrom) {
        return ctx.key().getText().substring(startFrom, ctx.key().getText().length()).toLowerCase();
    }

    private String getFirstLetterOfString(QueryParser.PropertyContext ctx) {
        return ctx.key().getText().substring(0, 1).toUpperCase();
    }

    public void esDeclaration(String where) {
        if (where.equalsIgnoreCase("from")) {
            this.setFromEs(true);
        } else if (where.equalsIgnoreCase("to") && isFromEs()) {
            logger.error("You cannot set two elastic instances in a query!");
            System.exit(-1);
        }
        this.setPrefix("es");
    }

    public void setDefaultValues() {
        if ((map.containsKey("esQuery") && !isFromEs()) || (map.containsKey("mongoQuery") && !isFromMongo())) {
            logger.error("You cannot set the query property in the TO statement!");
            System.exit(-1);
        }
        final Map<String, String> defaultProperties = new HashMap<>();
        defaultProperties.put("mongoPort", "27017");
        defaultProperties.put("mongoHost", "localhost");
        defaultProperties.put("esPort", "9300");
        defaultProperties.put("esHost", "localhost");
        defaultProperties.put("mongoQuery", "\"{}\"");
        defaultProperties.put("esQuery", "\"{\\\"query\\\":{\\\"match_all\\\":{}}}\"");
        defaultProperties.forEach((k, v) -> {
            final boolean exist = map.containsKey(k);
            if (!exist) this.addProperty(this.setKeyValue(k, v));
        });
    }

    public void initializePropertyList() {
        properties = new ArrayList<>();
        map = new HashMap<>();
    }

    private String setKeyValue(String key, String value) {
        map.put(key, value);
        return String.join(": ", key, value);
    }

    public void setAsCollectionName(String asColl) {
        String db = map.get("database");
        String coll = map.get("collection");
        String asDb = map.get("asDatabase");
        if (asDb.equals(db) && coll.equals(asColl)) {
            logger.error("Specify different db/index and collection/type!");
            System.exit(-1);
        }
        String newCollection = this.setKeyValue("asCollection", asColl);
        this.addProperty(newCollection);
    }

    public void setAsDatabaseName(String newDb) {
        String newdb = this.setKeyValue("asDatabase", newDb);
        this.addProperty(newdb);
    }
}
