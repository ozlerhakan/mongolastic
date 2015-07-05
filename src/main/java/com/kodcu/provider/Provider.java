package com.kodcu.provider;


import com.kodcu.service.BulkService;

/**
 * Created by Hakan on 6/30/2015.
 */
public abstract class Provider {

    public void transfer(final BulkService bulkService, final Runnable closeConnections) {
        long count = this.getCount();
        final int limit = 200;
        int skip = 0;

        if (count != 0)
            bulkService.dropDataSet();

        while (count >= limit) {
            String jsonContent = this.buildJSONContent(skip, limit);
            bulkService.proceed(jsonContent);
            count -= limit;
            skip += limit;
        }

        if (count > 0) {
            String jsonContent = this.buildJSONContent(skip, (int) count);
            bulkService.proceed(jsonContent);
        }

        closeConnections.run();
    }

    protected abstract long getCount();

    protected abstract String buildJSONContent(int skip, int limit);
}
