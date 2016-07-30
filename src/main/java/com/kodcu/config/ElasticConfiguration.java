package com.kodcu.config;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
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
        	Settings settings = null; 
        	if (config.getElastic().getAuth()!=null) {
        		settings = Settings.settingsBuilder()
                        .put("client.transport.ignore_cluster_name", true)
                        .put("client.transport.ping_timeout", "5s")
                        .put("client.transport.nodes_sampler_interval", "5s")
                        // added by YG for supporting auth with shield
                        .put("shield.user", "transport_client_user:" 
                        		+ config.getElastic().getAuth().getUser()+":"+config.getElastic().getAuth().getPwd()
                        		)
                        .build();
        	}
        	else {
        		settings = Settings.settingsBuilder()
                        .put("client.transport.ignore_cluster_name", true)
                        .put("client.transport.ping_timeout", "5s")
                        .put("client.transport.nodes_sampler_interval", "5s")
                        .build();
        	}
              
            InetSocketTransportAddress ista = new InetSocketTransportAddress(InetAddress.getByName(config.getElastic().getHost()), config.getElastic().getPort());
            client = TransportClient.builder().settings(settings).build().addTransportAddress(ista);
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
