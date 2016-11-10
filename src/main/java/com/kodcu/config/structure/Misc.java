package com.kodcu.config.structure;

/**
 * Created by Hakan on 1/16/2016.
 */
public class Misc {

    private String direction = "me";
    private Namespace dindex;
    private Namespace ctype;
    private Boolean dropDataset = true;
    private int batch = 200;

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

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

    public Boolean getDropDataset() {
        return this.dropDataset;
    }

    public void setDropDataset(Boolean dropDataset) {
        this.dropDataset = dropDataset;
    }

    @Override
    public String toString() {
        return "Misc{" +
                "batch=" + batch +
                ", direction='" + direction + '\'' +
                ", dindex=" + dindex +
                ", ctype=" + ctype +
                ", dropDataset=" + dropDataset +
                '}';
    }
}
