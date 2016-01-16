package com.kodcu.config;

import com.kodcu.config.yml.Elastic;
import com.kodcu.config.yml.Misc;
import com.kodcu.config.yml.Mongo;

/**
 * Created by Hakan on 5/19/2015.
 */
public class YamlConfiguration {

    private Misc misc;
    private Mongo mongo;
    private Elastic elastic;

    public Elastic getElastic() {
        return elastic;
    }

    public void setElastic(Elastic elastic) {
        this.elastic = elastic;
    }

    public Misc getMisc() {
        return misc;
    }

    public void setMisc(Misc misc) {
        this.misc = misc;
    }

    public Mongo getMongo() {
        return mongo;
    }

    public void setMongo(Mongo mongo) {
        this.mongo = mongo;
    }

    @Override
    public String toString() {
        return "{" +
                "elastic=" + elastic +
                ", misc=" + misc +
                ", mongo=" + mongo +
                '}';
    }
}