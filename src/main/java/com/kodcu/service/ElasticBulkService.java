package com.kodcu.service;

import com.kodcu.config.ElasticConfiguration;
import com.kodcu.config.YamlConfiguration;
import com.kodcu.listener.BulkProcessorListener;
import com.mongodb.client.MongoCursor;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public void proceed(List content) {
        try {
            logger.info("Transferring data began to elasticsearch.");
            final String indexName = config.getMisc().getDindex().getAs();
            final String typeName = config.getMisc().getCtype().getAs();
            MongoCursor<Document> cursor = (MongoCursor<Document>) content.get(0);
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Object id = doc.get("_id");
                IndexRequest indexRequest = new IndexRequest(indexName, typeName, String.valueOf(id));
                doc.remove("_id");
                indexRequest.source(doc.toJson().getBytes(Charset.forName("UTF-8")));
                bulkProcessor.add(indexRequest);
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex.fillInStackTrace());
        }

    }

    private void initialize() {
        bulkProcessor = BulkProcessor.builder(client.getClient(), new BulkProcessorListener())
                .setBulkActions(config.getMisc().getBatch())
                .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
                .build();
    }

    @Override
    public void close() {
        try {
            bulkProcessor.awaitClose(10, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
    }

    @Override
    public void dropDataSet() {
        final String indexName = config.getMisc().getDindex().getAs();
        IndicesAdminClient admin = client.getClient().admin().indices();
        IndicesExistsRequestBuilder builder = admin.prepareExists(indexName);
        if (builder.execute().actionGet().isExists()) {
            DeleteIndexResponse delete = admin.delete(new DeleteIndexRequest(indexName)).actionGet();
            if (delete.isAcknowledged())
                logger.info(String.format("The current index %s was deleted.", indexName));
            else
                logger.info(String.format("The current index %s was not deleted.", indexName));
        }
    }

}
