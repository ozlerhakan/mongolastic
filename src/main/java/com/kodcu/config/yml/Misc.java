package com.kodcu.config.yml;

/**
 * Created by Hakan on 1/16/2016.
 */
public class Misc {

    private String direction = "me";
    private Namespace dindex;
    private Namespace ctype;

    public Namespace getCtype() {
        return ctype;
    }

    public void setCtype(Namespace ctype) {
        this.ctype = ctype;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Namespace getDindex() {
        return dindex;
    }

    public void setDindex(Namespace dindex) {
        this.dindex = dindex;
    }

    @Override
    public String toString() {
        return "Misc{" +
                "ctype=" + ctype +
                ", direction='" + direction + '\'' +
                ", dindex=" + dindex +
                '}';
    }
}
