package com.kodcu.service;

import java.util.List;

/**
 * Created by Hakan on 6/30/2015.
 */
public interface BulkService {

    void proceed(List content);

    void dropDataSet();

    void close();
}
