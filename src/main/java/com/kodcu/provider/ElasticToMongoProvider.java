package com.kodcu.provider;

import com.kodcu.config.ElasticConfiguration;
import com.kodcu.config.YamlConfiguration;
import com.kodcu.converter.JsonBuilder;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.index.search.MultiMatchQuery;
import org.elasticsearch.search.SearchHit;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.Map;
import java.util.Set;

/**
 * Created by Hakan on 6/29/2015.
 */
public class ElasticToMongoProvider implements Provider {

    private final Logger logger = Logger.getLogger(ElasticToMongoProvider.class);
    private final ElasticConfiguration elastic;
    private final YamlConfiguration config;
    private final JsonBuilder builder;

    public ElasticToMongoProvider(final ElasticConfiguration elastic, final YamlConfiguration config, final JsonBuilder builder) {
        this.elastic = elastic;
        this.config = config;
        this.builder = builder;
    }

    @Override
    public long getCount() {
        long count = 0;
        IndicesAdminClient admin = elastic.getClient().admin().indices();
        IndicesExistsRequestBuilder builder = admin.prepareExists(config.getMisc().getDindex().getName());
        if (builder.execute().actionGet().isExists()) {
            SearchResponse response = elastic.getClient().prepareSearch(config.getMisc().getDindex().getName())
                    .setTypes(config.getMisc().getCtype().getName())
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setSize(0)
                    .execute().actionGet();
            count = response.getHits().getTotalHits();
        } else {
            logger.info("Index/Type does not exist or does not contain the record");
            System.exit(-1);
        }
        return count;
    }

    @Override
    public String buildJSONContent(int skip, int limit) {

        SearchResponse response = elastic.getClient().prepareSearch(config.getMisc().getDindex().getName())
                .setTypes(config.getMisc().getCtype().getName())
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setFrom(skip).setSize(limit)
                .execute().actionGet();

        StringBuilder sb = new StringBuilder();
        for (SearchHit hit : response.getHits().getHits()) {
            Set<Map.Entry<String, Object>> result = hit.getSource().entrySet();
            JsonObjectBuilder jsonObj = Json.createObjectBuilder();
            jsonObj.add("_id", Json.createObjectBuilder().add("$oid", hit.getId()));
            result.stream().forEach(entry -> {
                builder.buildJson(jsonObj, entry, sb);
            });
            sb.append(jsonObj.build().toString());
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }
}
