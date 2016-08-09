package com.kodcu.config;

import java.util.Objects;
import java.util.Optional;
import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;

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

            Builder settingsBuilder = Settings.builder();

            settingsBuilder.put("client.transport.ping_timeout", "15s");
            settingsBuilder.put("client.transport.nodes_sampler_interval", "5s");
            // YG: to ensure reliable connection & resolve NoNodeAvailableException
            settingsBuilder.put("client.transport.sniff", true);
            settingsBuilder.put("network.bind_host", 0);

            // YG: for supporting ES Auth with ES Shield
            Optional.ofNullable(config.getElastic().getAuth()).ifPresent(auth -> settingsBuilder.put("shield.user", String.join(":", auth.getUser(), auth.getPwd())));

            if (Objects.nonNull(config.getElastic().getClusterName())) {
                settingsBuilder.put("cluster.name", config.getElastic().getClusterName());
            } else {
                settingsBuilder.put("client.transport.ignore_cluster_name", true);
            }

            InetSocketTransportAddress ista = new InetSocketTransportAddress(InetAddress.getByName(config.getElastic().getHost()), config.getElastic().getPort());
            client = TransportClient.builder().settings(settingsBuilder.build()).build().addTransportAddress(ista);
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

}
