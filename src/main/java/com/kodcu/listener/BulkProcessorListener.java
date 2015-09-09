package com.kodcu.listener;

import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;

/**
 * Created by Hakan on 5/21/2015.
 */
public class BulkProcessorListener implements BulkProcessor.Listener {

    private final Logger logger = Logger.getLogger(BulkProcessorListener.class);

    @Override
    public void beforeBulk(long executionId, BulkRequest request) {
    }

    @Override
    public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
        logger.info(String.format("Data transfer successfully terminated.(%d)", response.getItems().length));
    }

    @Override
    public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
        logger.error("Transfer failed.");
        logger.error(failure.getMessage(), failure.fillInStackTrace());
    }
}
