package com.kodcu.service;

import com.kodcu.config.ElasticConfiguration;
import com.kodcu.config.YamlConfiguration;
import com.kodcu.listener.BulkProcessorListener;
import com.kodcu.util.Constants;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hakan on 5/21/2015.
 */
public class ElasticBulkService implements BulkService {

    private final Logger logger = Logger.getLogger(ElasticBulkService.class);
    private final YamlConfiguration config;
    private final ElasticConfiguration client;
    private BulkProcessor bulkProcessor;

    public ElasticBulkService(final YamlConfiguration config, final ElasticConfiguration client) {
        this.config = config;
        this.client = client;
        this.initialize();
    }

    @Override
    public void proceed(String jsonContent) {
        final Pattern pattern = Pattern.compile(Constants.CREATE_ACTION);

        try {
            logger.info("Transferring data began to elasticsearch.");
            final String indexName = config.getAsDatabase();
            final String typeName = config.getAsCollection();
            String id = null;

            for (String line : jsonContent.split(System.lineSeparator())) {
                final Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    id = matcher.group(1);
                } else {
                    IndexRequest indexRequest = new IndexRequest(indexName, typeName, id);
                    indexRequest.source(line.getBytes(Charset.forName("UTF-8")));
                    bulkProcessor.add(indexRequest);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
    }

    private void initialize() {
        bulkProcessor = BulkProcessor.builder(client.getClient(), new BulkProcessorListener())
                .setBulkActions(200)
                .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
                .build();
    }

    @Override
    public void close() {
        try {
            bulkProcessor.awaitClose(60, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
    }

    @Override
    public void dropDataSet() {
        IndicesAdminClient admin = client.getClient().admin().indices();
        IndicesExistsRequestBuilder builder = admin.prepareExists(config.getAsDatabase());
        if (builder.execute().actionGet().isExists()) {
            DeleteIndexResponse delete = admin.delete(new DeleteIndexRequest(config.getAsDatabase())).actionGet();
            if (delete.isAcknowledged())
                logger.info(String.format("The current index %s was deleted.", config.getAsDatabase()));
            else
                logger.info(String.format("The current index %s was not deleted.", config.getAsDatabase()));
        }
    }

}
