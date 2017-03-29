package com.kodcu.provider;

import com.kodcu.config.ElasticConfiguration;
import com.kodcu.config.YamlConfiguration;
import org.bson.Document;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Hakan on 6/29/2015.
 */
public class ElasticToMongoProvider implements Provider {

    private final Logger logger = LoggerFactory.getLogger(ElasticToMongoProvider.class);
    private final ElasticConfiguration elastic;
    private final YamlConfiguration config;
    private SearchResponse response;

    public ElasticToMongoProvider(final ElasticConfiguration elastic, final YamlConfiguration config) {
        this.elastic = elastic;
        this.config = config;
    }

    @Override
    public long getCount() {
        long count = 0;
        IndicesAdminClient admin = elastic.getClient().admin().indices();
        IndicesExistsRequestBuilder builder = admin.prepareExists(config.getMisc().getDindex().getName());
        if (builder.execute().actionGet().isExists()) {
            SearchResponse countResponse = elastic.getClient().prepareSearch(config.getMisc().getDindex().getName())
                    .setTypes(config.getMisc().getCtype().getName())
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setSize(0)
                    .execute().actionGet();
            count = countResponse.getHits().getTotalHits();
        } else {
            logger.info("Index/Type does not exist or does not contain the record");
            System.exit(-1);
        }

        logger.info("Elastic Index/Type count: " + count);
        return count;
    }

    @Override
    public List buildJSONContent(int skip, int limit) {

        if (Objects.isNull(response)) {
            response = elastic.getClient().prepareSearch(config.getMisc().getDindex().getName())
                    .setTypes(config.getMisc().getCtype().getName())
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setScroll(new TimeValue(60000))
                    .setSize(limit)
                    .execute().actionGet();
        }
        else {
            response = elastic.getClient()
                    .prepareSearchScroll(response.getScrollId())
                    .setScroll(new TimeValue(60000))
                    .execute().actionGet();
        }

        return Arrays.stream(response.getHits().getHits())
                .map(hit -> new Document(hit.getSource()))
                .collect(Collectors.toList());
    }
}
