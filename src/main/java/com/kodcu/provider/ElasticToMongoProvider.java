package com.kodcu.provider;

import com.kodcu.config.ElasticConfiguration;
import com.kodcu.config.YamlConfiguration;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hakan on 6/29/2015.
 */
public class ElasticToMongoProvider implements Provider {

    private final Logger logger = Logger.getLogger(ElasticToMongoProvider.class);
    private final ElasticConfiguration elastic;
    private final YamlConfiguration config;

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

        logger.info("Elastic Index/Type count: " + count);
        return count;
    }

    @Override
    public List buildJSONContent(int skip, int limit) {

        SearchResponse response = elastic.getClient().prepareSearch(config.getMisc().getDindex().getName())
                .setTypes(config.getMisc().getCtype().getName())
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setScroll(new TimeValue(10000))
                .setFrom(skip).setSize(limit)
                .execute().actionGet();

        List<Document> documents = new ArrayList<Document>();
        for (SearchHit hit : response.getHits().getHits()) {
            documents.add(new Document(hit.getSource()));
        }
        return documents;
    }
}
