package com.kodcu.service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.bson.BsonType;
import org.bson.Document;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.Codec;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.Encoder;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;

import com.kodcu.config.ElasticConfiguration;
import com.kodcu.config.YamlConfiguration;
import com.kodcu.listener.BulkProcessorListener;
import com.kodcu.util.codecs.CustomDateCodec;
import com.mongodb.MongoClient;

/**
 * Created by Hakan on 5/21/2015.
 */
public class ElasticBulkService implements BulkService {

    private final Logger logger = Logger.getLogger(ElasticBulkService.class);
    private final YamlConfiguration config;
    private final ElasticConfiguration client;
    private final BulkProcessor bulkProcessor;
    private final Encoder<Document> encoder;

    public ElasticBulkService(final YamlConfiguration config, final ElasticConfiguration client) {
        this.config = config;
        this.client = client;

        this.bulkProcessor = BulkProcessor.builder(client.getClient(), new BulkProcessorListener())
            .setBulkActions(config.getMisc().getBatch())
            .setFlushInterval(TimeValue.timeValueSeconds(5))
            .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
            .build();

        encoder = getEncoder();
    }

    @Override
    public void proceed(List content) {
        try {
            logger.info("Transferring data began to elasticsearch.");
            final String indexName = config.getMisc().getDindex().getAs();
            final String typeName = config.getMisc().getCtype().getAs();
            for (Object o : content) {
                Document doc = (Document) o;
                Object id = doc.get("_id");
                IndexRequest indexRequest = new IndexRequest(indexName, typeName, String.valueOf(id));
                doc.remove("_id");
                indexRequest.source(doc.toJson(encoder).getBytes(Charset.forName("UTF-8")));
                bulkProcessor.add(indexRequest);
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex.fillInStackTrace());
        }
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

    /**
     * Customizations for the document.toJson output.
     *
     * http://mongodb.github.io/mongo-java-driver/3.0/bson/codecs/
     *
     * @return the toJson encoder.
     */
  private Encoder<Document> getEncoder() {
        Map<BsonType, Class<?>> replacements = new HashMap<BsonType, Class<?>>();
        ArrayList<Codec<?>> codecs = new ArrayList<>();

        if (config.getElastic().getDateFormat() != null) {
            // Replace default DateCodec class to use the custom date formatter.
            replacements.put(BsonType.DATE_TIME, CustomDateCodec.class);
            codecs.add(new CustomDateCodec(config.getElastic().getDateFormat()));
        }

        if (codecs.size() > 0) {
            BsonTypeClassMap bsonTypeClassMap = new BsonTypeClassMap(replacements);
            DocumentCodecProvider documentCodecProvider = new DocumentCodecProvider(bsonTypeClassMap);

            CodecRegistry codecRegistry = codecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(codecs),
                CodecRegistries.fromProviders(documentCodecProvider),
                MongoClient.getDefaultCodecRegistry());

            return new DocumentCodec(codecRegistry, bsonTypeClassMap);
        } else {
            return new DocumentCodec();
        }
    }
}
