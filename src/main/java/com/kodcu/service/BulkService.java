package com.kodcu.service;

/**
 * Created by Hakan on 6/30/2015.
 */
public interface BulkService {

    void proceed(String jsonContent);

    void dropDataSet();

    void close();
}
