package com.gokdenizozkan.solrsamplejavaapp;

import com.gokdenizozkan.solrsamplejavaapp.config.SolrClientProvider;
import com.gokdenizozkan.solrsamplejavaapp.engine.IndexingEngine;
import com.gokdenizozkan.solrsamplejavaapp.engine.RadarEngine;

public class App {
    public static void main(String[] args) {
        String solrBaseUrl = "http://localhost:8983/solr";
        SolrClientProvider provider = new SolrClientProvider();

        IndexingEngine indexingEngine = new IndexingEngine(provider, solrBaseUrl);
        RadarEngine radarEngine = new RadarEngine(provider, solrBaseUrl);
    }
}
