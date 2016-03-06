package com.kodcu.provider;


import com.kodcu.config.YamlConfiguration;
import com.kodcu.service.BulkService;
import org.bson.Document;

import java.util.List;

/**
 * Created by Hakan on 6/30/2015.
 */
public interface Provider {

    default void transfer(final BulkService bulkService,YamlConfiguration config,  final Runnable closeConnections) {
        long count = this.getCount();
        final int limit = config.getMisc().getBatch();
        int skip = 0;

        if (count != 0)
            bulkService.dropDataSet();

        while (count >= limit) {
            List content = this.buildJSONContent(skip, limit);
            bulkService.proceed(content);
            count -= limit;
            skip += limit;
        }

        if (count > 0) {
            List content = this.buildJSONContent(skip, (int) count);
            bulkService.proceed(content);
        }

        closeConnections.run();
    }

    long getCount();

    List<Document> buildJSONContent(int skip, int limit);
}
