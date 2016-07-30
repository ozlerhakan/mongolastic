package com.kodcu.config.yml;

/**
 * Created by Hakan on 1/16/2016.
 */
public class Elastic {

    private String host;
    private int port;
    private String dateFormat;
    private Boolean longToString = false;
    
    // added by YG 
    private Auth auth;
    public Auth getAuth() {
        return auth;
    }
    public void setAuth(Auth auth) {
        this.auth = auth;
    }
    
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setLongToString(Boolean longToString) {
        this.longToString = longToString;
    }

    public Boolean getLongToString() {
        return this.longToString;
    }

    @Override
    public String toString() {
        return "Elastic{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", dateFormat=" + dateFormat +
                ", longToString=" + longToString +
                ", auth=" + auth +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Elastic elastic = (Elastic) o;

        if (port != elastic.port) return false;
        return host != null ? host.equals(elastic.host) : elastic.host == null;

    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }
}
