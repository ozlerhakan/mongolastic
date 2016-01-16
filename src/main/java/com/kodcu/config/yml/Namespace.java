package com.kodcu.config.yml;

/**
 * Created by Hakan on 1/16/2016.
 */
public class Namespace {
    private String name;
    private String as;

    public String getAs() {
        return as;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
            this.name = name;
        }

    @Override
    public String toString() {
        return "Namespace{" +
                "as='" + as + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
