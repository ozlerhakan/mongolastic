package com.kodcu.config;

/**
 * Created by Hakan on 5/19/2015.
 */
public class YamlConfiguration {

    private String mongoHost;
    private int mongoPort;
    private String database;
    private String collection;
    private String outFile;
    private String esHost;
    private int esTransPort;
    private boolean enableBulk;

    private YamlConfiguration() {
    }

    public boolean isEnableBulk() {
        return enableBulk;
    }

    public void setEnableBulk(boolean enableBulk) {
        this.enableBulk = enableBulk;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getOutFile() {
        return outFile;
    }

    public void setOutFile(String outFile) {
        this.outFile = outFile;
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

    public int getEsTransPort() {
        return esTransPort;
    }

    public void setEsTransPort(int esTransPort) {
        this.esTransPort = esTransPort;
    }

    @Override
    public String toString() {
        return "YamlConfiguration{" +
                "collection='" + collection + '\'' +
                ", mongoHost='" + mongoHost + '\'' +
                ", mongoPort=" + mongoPort +
                ", database='" + database + '\'' +
                ", outFile='" + outFile + '\'' +
                ", esHost='" + esHost + '\'' +
                ", esTransPort=" + esTransPort +
                ", enableBulk=" + enableBulk +
                '}';
    }
}