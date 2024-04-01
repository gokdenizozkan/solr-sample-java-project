package com.gokdenizozkan.solrsamplejavaapp.solrquery;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

public class SolrFetchQuery {
    private final SolrQuery query;
    private final String collectionName;

    private SolrFetchQuery(String collectionName) {
        this.query = new SolrQuery();
        this.collectionName = collectionName;
    }

    public static SolrFetchQuery of(String collectionName) {
        return new SolrFetchQuery(collectionName);
    }

    public SolrFetchQuery query(String query) {
        this.query.setQuery(query);
        return this;
    }

    public SolrFetchQuery queryAll() {
        query.setQuery("*:*");
        return this;
    }

    public SolrFetchQuery queryById(String id) {
        query.setQuery(new StringBuilder("id:").append(id).toString());
        return this;
    }

    public SolrQuery toQuery() {
        return query;
    }

    public String toQueryString() {
        return query.toQueryString();
    }

    public QueryResponse _process(SolrClient client) {
        try {
            return client.query(collectionName, query);
        } catch (Exception e) {
            return new QueryResponse();
        }
    }

    public SolrDocument _processAndGetFirstResult(SolrClient client) {
        try {
            SolrDocument found = client.query(collectionName, query).getResults().getFirst();
            return found;
        } catch (Exception e) {
            return new SolrDocument();
        }
    }
}
