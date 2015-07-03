package com.kodcu.config;

/**
 * Created by Hakan on 5/19/2015.
 */
public class YamlConfiguration {

    private String collection;
    private String database;
    private String asCollection;
    private String asDatabase;
    private String esHost;
    private String mongoHost;
    private int esPort;
    private int mongoPort;
    private boolean fromMongo;

    private YamlConfiguration() {
    }

    public String getAsCollection() {
        return asCollection;
    }

    public void setAsCollection(String asCollection) {
        this.asCollection = asCollection;
    }

    public String getAsDatabase() {
        return asDatabase;
    }

    public void setAsDatabase(String asDatabase) {
        this.asDatabase = asDatabase;
    }

    public boolean isFromMongo() {
        return fromMongo;
    }

    public void setFromMongo(boolean fromMongo) {
        this.fromMongo = fromMongo;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String c) {
        this.collection = c;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String db) {
        this.database = db;
    }

    public String getMongoHost() {
        return mongoHost;
    }

    public void setMongoHost(String mongoHost) {
        this.mongoHost = mongoHost;
    }

    public int getMongoPort() {
        return mongoPort;
    }

    public void setMongoPort(int mongoPort) {
        this.mongoPort = mongoPort;
    }

    public String getEsHost() {
        return esHost;
    }

    public void setEsHost(String esHost) {
        this.esHost = esHost;
    }

    public int getEsPort() {
        return esPort;
    }

    public void setEsPort(int esTransPort) {
        this.esPort = esTransPort;
    }

    @Override
    public String toString() {
        return "YamlConfiguration{" +
                "collection='" + collection + '\'' +
                ", mongoHost='" + mongoHost + '\'' +
                ", mongoPort=" + mongoPort +
                ", database='" + database + '\'' +
                ", esHost='" + esHost + '\'' +
                ", esTransPort=" + esPort +
                '}';
    }
}