package com.gokdenizozkan.solrsamplejavaapp.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.Http2SolrClient;

public class SolrClientProvider {
    private SolrClient client;

    public SolrClient http2(String solrBaseUrl) {
        if (client != null) {
            return client;
        }
        client = new Http2SolrClient.Builder(solrBaseUrl).build();
        return client;
    }

    public SolrClient http2(String solrBaseUrl, boolean override) {
        if (!override) {
            return http2(solrBaseUrl);
        }
        client = new Http2SolrClient.Builder(solrBaseUrl).build();
        return client;
    }
}
