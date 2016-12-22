package com.kodcu.listener;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Hakan on 5/21/2015.
 */
public class BulkProcessorListener implements BulkProcessor.Listener {

    private final Logger logger = LoggerFactory.getLogger(BulkProcessorListener.class);

    @Override
    public void beforeBulk(long executionId, BulkRequest request) {
    }

    @Override
    public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
        if (response.hasFailures()) {
            logger.error(response.buildFailureMessage());
        } else {
            logger.info(String.format("Data transfer successfully terminated.(%d)", response.getItems().length));
        }

    }

    @Override
    public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
        logger.error("Transfer failed.");
        logger.error(failure.getMessage(), failure.fillInStackTrace());
    }
}
