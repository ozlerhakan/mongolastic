package com.kodcu.config;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * Created by hakdogan on 21/05/15.
 */
public class ElasticConfiguration {

    private final Logger logger = Logger.getLogger(ElasticConfiguration.class);
    private final YamlConfiguration config;
    private Client client;

    public ElasticConfiguration(final YamlConfiguration config) {
        this.config = config;
        this.prepareClient();
    }

    private void prepareClient() {
        try {
            Settings settings = ImmutableSettings.settingsBuilder()
                    .put("client.transport.ignore_cluster_name", true)
                    .put("client.transport.ping_timeout", 5000)
                    .put("client.transport.nodes_sampler_interval", 5000)
                    .build();
            InetSocketTransportAddress ista = new InetSocketTransportAddress(config.getEsHost(), config.getEsTransPort());
            client = new TransportClient(settings, false).addTransportAddress(ista);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex.fillInStackTrace());
        }
    }

    public void closeNode() {
        client.close();
    }

    public Client getClient() {
        return client;
    }

    public void printThis() {
        System.out.println(this);
    }

}
