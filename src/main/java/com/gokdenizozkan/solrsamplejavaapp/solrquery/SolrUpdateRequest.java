package com.gokdenizozkan.solrsamplejavaapp.solrquery;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

public class SolrUpdateRequest {
    private final UpdateRequest request;
    private final String collectionName;

    private SolrUpdateRequest(String collectionName) {
        this.request = new UpdateRequest();
        this.request.setMethod(UpdateRequest.METHOD.POST);

        this.collectionName = collectionName;
    }

    public static SolrUpdateRequest of(String collectionName) {
        return new SolrUpdateRequest(collectionName);
    }

    public SolrUpdateRequest add(SolrInputDocument document) {
        request.add(document);
        return this;
    }

    public SolrUpdateRequest setCommitTrue() {
        request.setParam("commit", "true");
        return this;
    }

    public SolrUpdateRequest setOverwriteTrue() {
        request.setParam("overwrite", "true");
        return this;
    }

    public SolrUpdateRequest setCommitWithin(Integer milliseconds) {
        request.setCommitWithin(milliseconds);
        return this;
    }

    public UpdateRequest toRequest() {
        return request;
    }

    public UpdateResponse _process(SolrClient client) {
        try {
            return request.process(client, collectionName);
        } catch (Exception e) {
            return new UpdateResponse();
        }
    }
}
