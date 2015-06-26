package com.kodcu.config;

/**
 * Created by Hakan on 5/19/2015.
 */
public class YamlConfiguration {

    private String esHost;
    private String database;
    private String fileName;
    private String mongoHost;
    private String collection;
    private int esPort;
    private int mongoPort;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String outFile) {
        this.fileName = outFile;
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
                ", fileName='" + fileName + '\'' +
                ", esHost='" + esHost + '\'' +
                ", esTransPort=" + esPort +
                ", enableBulk=" + enableBulk +
                '}';
    }
}