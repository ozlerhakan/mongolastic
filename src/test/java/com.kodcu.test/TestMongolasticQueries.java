package com.kodcu.test;

import com.kodcu.config.FileConfiguration;
import com.kodcu.config.YamlConfiguration;
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
                {new FileConfiguration(new String[]{"src/test/resources/query1.mongolastic"}), createQueryConfiguration1()},
                {new FileConfiguration(new String[]{"src/test/resources/query2.mongolastic"}), createQueryConfiguration2()}
        });
    }

    private static YamlConfiguration createQueryConfiguration1() {
        YamlConfiguration query = new YamlConfiguration();
        query.setDatabase("twitter");
        query.setCollection("tweets");
        query.setAsDatabase("kodcu");
        query.setAsCollection("tweets");
        query.setMongoHost("localhost");
        query.setMongoPort(27017);
        query.setEsHost("localhost");
        query.setEsPort(9300);
        query.setMongoQuery("{ 'user.name' : 'kodcu.com'}");
        query.setEsQuery("{\"query\":{\"match_all\":{}}}");
        query.setFromMongo(true);
        return query;
    }

    private static YamlConfiguration createQueryConfiguration2() {
        YamlConfiguration query = new YamlConfiguration();
        query.setDatabase("twitter");
        query.setCollection("tweets");
        query.setAsDatabase("barca");
        query.setAsCollection("tweets");
        query.setMongoHost("localhost");
        query.setMongoPort(27017);
        query.setEsHost("localhost");
        query.setEsPort(9300);
        query.setMongoQuery("{}");
        query.setEsQuery("{\"query\": {\"match\": {\"user.screen_name\": \"FCBarcelona\"}}}");
        query.setFromMongo(false);
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
        assertThat(actual.getMongoQuery(), is(expected.getMongoQuery()));
        assertThat(actual.getEsQuery(), is(expected.getEsQuery()));
        assertThat(actual.getMongoHost(), is(expected.getMongoHost()));
        assertThat(actual.getMongoPort(), is(expected.getMongoPort()));
        assertThat(actual.getEsHost(), is(expected.getEsHost()));
        assertThat(actual.getEsPort(), is(expected.getEsPort()));
        assertThat(actual.getDatabase(), is(expected.getDatabase()));
        assertThat(actual.getAsDatabase(), is(expected.getAsDatabase()));
        assertThat(actual.getCollection(), is(expected.getCollection()));
        assertThat(actual.getAsCollection(), is(expected.getAsCollection()));
    }
}
