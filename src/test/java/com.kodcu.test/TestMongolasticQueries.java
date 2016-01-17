package com.kodcu.test;

import com.kodcu.config.FileConfiguration;
import com.kodcu.config.YamlConfiguration;
import com.kodcu.config.yml.Elastic;
import com.kodcu.config.yml.Misc;
import com.kodcu.config.yml.Mongo;
import com.kodcu.config.yml.Namespace;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by Hakan on 8/24/2015.
 */
@RunWith(Parameterized.class)
public class TestMongolasticQueries {

    @Parameterized.Parameters(name = "{index}: ({0})={1}")
    public static Iterable<Object[]> queries() throws Exception {
        return Arrays.asList(new Object[][]{
                {new FileConfiguration(new String[]{"-f","src/test/resources/conf1"}), createQueryConfiguration1()},
                {new FileConfiguration(new String[]{"-f","src/test/resources/conf2"}), createQueryConfiguration2()}
        });
    }

    private static YamlConfiguration createQueryConfiguration1() {
        YamlConfiguration query = new YamlConfiguration();

        Misc misc = new Misc();
        Namespace db = new Namespace();
        db.setName("twitter");
        db.setAs("kodcu");
        misc.setDindex(db);
        Namespace c = new Namespace();
        c.setName("tweets");
        c.setAs("tweets");
        misc.setCtype(c);
        query.setMisc(misc);

        Mongo mongod = new Mongo();
        mongod.setHost("localhost");
        mongod.setPort(27017);
        mongod.setQuery("{ 'user.name' : 'kodcu.com'}");
        query.setMongo(mongod);

        Elastic es = new Elastic();
        es.setHost("localhost");
        es.setPort(9300);
        query.setElastic(es);
        return query;
    }

    private static YamlConfiguration createQueryConfiguration2() {
        YamlConfiguration query = new YamlConfiguration();

        Misc misc = new Misc();
        Namespace db = new Namespace();
        db.setName("twitter");
        db.setAs("twitter");
        misc.setDindex(db);
        Namespace c = new Namespace();
        c.setName("tweets");
        c.setAs("posts");
        misc.setCtype(c);
        misc.setDirection("em");
        query.setMisc(misc);

        Mongo mongod = new Mongo();
        mongod.setHost("127.0.0.1");
        mongod.setPort(27017);
        query.setMongo(mongod);

        Elastic es = new Elastic();
        es.setHost("127.0.0.1");
        es.setPort(9300);
        query.setElastic(es);
        return query;
    }

    private final FileConfiguration config;
    private final YamlConfiguration expected;

    public TestMongolasticQueries(final FileConfiguration config, final YamlConfiguration expected) {
        super();
        this.config = config;
        this.expected = expected;
    }

    @Test
    public void shouldProceedQueries() {
        YamlConfiguration actual = config.getFileContent();
        assertThat(actual, notNullValue());
        assertThat(actual.getMongo().getQuery(), is(expected.getMongo().getQuery()));
        assertThat(actual.getMongo().getHost(), is(expected.getMongo().getHost()));
        assertThat(actual.getMongo().getPort(), is(expected.getMongo().getPort()));
        assertThat(actual.getElastic().getHost(), is(expected.getElastic().getHost()));
        assertThat(actual.getElastic().getPort(), is(expected.getElastic().getPort()));
        assertThat(actual.getMisc().getDindex().getName(), is(expected.getMisc().getDindex().getName()));
        assertThat(actual.getMisc().getDindex().getAs(), is(expected.getMisc().getDindex().getAs()));
        assertThat(actual.getMisc().getCtype().getName(), is(expected.getMisc().getCtype().getName()));
        assertThat(actual.getMisc().getCtype().getAs(), is(expected.getMisc().getCtype().getAs()));
    }
}
