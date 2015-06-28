package com.kodcu.config;

/**
 * Created by Hakan on 5/19/2015.
 */
public class YamlConfiguration {

    private String c;
    private String db;
    private String esHost;
    private String fileName;
    private String mongoHost;
    private int esPort;
    private int mongoPort;
    private boolean enableBulk;
    private boolean fromMongo;

    private YamlConfiguration() {
    }

    public boolean isFromMongo() {
        return fromMongo;
    }

    public void setFromMongo(boolean fromMongo) {
        this.fromMongo = fromMongo;
    }

    public boolean isEnableBulk() {
        return enableBulk;
    }

    public void setEnableBulk(boolean enableBulk) {
        this.enableBulk = enableBulk;
    }

    public String getCollection() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getDatabase() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
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
                "collection='" + c + '\'' +
                ", mongoHost='" + mongoHost + '\'' +
                ", mongoPort=" + mongoPort +
                ", database='" + db + '\'' +
                ", fileName='" + fileName + '\'' +
                ", esHost='" + esHost + '\'' +
                ", esTransPort=" + esPort +
                ", enableBulk=" + enableBulk +
                '}';
    }
}