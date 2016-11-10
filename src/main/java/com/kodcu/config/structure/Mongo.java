package com.kodcu.config.structure;

/**
 * Created by Hakan on 1/16/2016.
 */
public class Mongo {

    private String host;
    private int port;
    private String query = "{}";
    private Auth auth;

    @Override
    public String toString() {
        return "Mongo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", query='" + query + '\'' +
                ", auth=" + auth +
                '}';
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
