package com.kodcu.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * Created by hakdogan on 21/05/15.
 */
public class ElasticConfiguration {

    private final Logger logger = LogManager.getLogger(ElasticConfiguration.class);
    private final YamlConfiguration config;
    private Client client;

    public ElasticConfiguration(final YamlConfiguration config) {
        this.config = config;
        this.prepareClient();
    }

    private void prepareClient() {

        Builder settingsBuilder = applySettings();
        try {
            TransportAddress ista = new TransportAddress(InetAddress.getByName(config.getElastic().getHost()), config.getElastic().getPort());
            client = new PreBuiltTransportClient(settingsBuilder.build())
                    .addTransportAddress(ista);

        } catch (UnknownHostException ex) {
            logger.error(ex.getMessage(), ex);
            System.exit(-1);
        }
    }

    private Builder applySettings() {
        Builder settingsBuilder = Settings.builder();

        settingsBuilder.put("client.transport.ping_timeout", "15s");
        settingsBuilder.put("client.transport.nodes_sampler_interval", "5s");
        // YG: to ensure reliable connection & resolve NoNodeAvailableException
        settingsBuilder.put("client.transport.sniff", true);
        settingsBuilder.put("network.bind_host", 0);

        // YG: for supporting ES Auth with ES Shield
        Optional.ofNullable(config.getElastic().getAuth())
                .ifPresent(auth -> settingsBuilder.put("xpack.security.user", String.join(":", auth.getUser(), auth.getPwd())));

        if (Objects.nonNull(config.getElastic().getClusterName())) {
            settingsBuilder.put("cluster.name", config.getElastic().getClusterName());
        } else {
            settingsBuilder.put("client.transport.ignore_cluster_name", true);
        }
        return settingsBuilder;
    }

    public void closeNode() {
        client.close();
    }

    public Client getClient() {
        return client;
    }

}
