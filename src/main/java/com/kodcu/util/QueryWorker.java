package com.kodcu.util;

import com.kodcu.lang.QueryParser;
import org.apache.log4j.Logger;

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
    private boolean esExist;
    private boolean mongoExist;

    public boolean isEsExist() {
        return esExist;
    }

    public void setEsExist(boolean esExist) {
        this.esExist = esExist;
    }

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

    public boolean isMongoExist() {
        return mongoExist;
    }

    public void setMongoExist(boolean mongoExist) {
        this.mongoExist = mongoExist;
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
        this.setMongoExist(true);
    }

    public void addKeyValue(QueryParser.PropertyContext ctx) {
        String key = String.join("",
                getPrefix(),
                this.getFirstLetterOfString(ctx),
                this.getSubString(ctx, 1));
        final String property = this.setKeyValue(key, ctx.value().getText());
        boolean duplicateProperty = this.getProperties().stream().anyMatch(p -> p.startsWith(key));
        if (duplicateProperty) {
            logger.error(String.format("You cannot define the %s more than once!", ctx.key().getText()));
            System.exit(-1);
        }
        this.addProperty(property);
    }

    public String getSubString(QueryParser.PropertyContext ctx, int startFrom) {
        return ctx.key().getText().substring(startFrom, ctx.key().getText().length()).toLowerCase();
    }

    public String getFirstLetterOfString(QueryParser.PropertyContext ctx) {
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
        this.setEsExist(true);
    }

    public void setDefaultValues() {
        final Map<String, String> defaultProperties = new HashMap<>();
        defaultProperties.put("mongoPort", "27017");
        defaultProperties.put("mongoHost", "localhost");
        defaultProperties.put("esPort", "9300");
        defaultProperties.put("esHost", "localhost");
        defaultProperties.forEach((k, v) -> {
            final boolean exist = this.getProperties().stream().anyMatch(p -> p.startsWith(k));
            if (!exist && isMongoExist() && k.startsWith("mongo"))
                this.addProperty(this.setKeyValue(k, v));
            if (!exist && isEsExist() && k.startsWith("es"))
                this.addProperty(this.setKeyValue(k, v));
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
        String newcollection = this.setKeyValue("asCollection", asColl);
        this.addProperty(newcollection);
    }

    public void setAsDatabaseName(String newDb) {
        String newdb = this.setKeyValue("asDatabase", newDb);
        this.addProperty(newdb);
    }
}
