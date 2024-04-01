package com.gokdenizozkan.solrsamplejavaapp.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.gokdenizozkan.solrsamplejavaapp.config.SolrClientProvider;
import com.gokdenizozkan.solrsamplejavaapp.solrquery.SolrFetchQuery;
import com.gokdenizozkan.solrsamplejavaapp.solrquery.SolrUpdateRequest;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class IndexingEngine {
    private final SolrClient client;

    public IndexingEngine(SolrClientProvider provider, String solrBaseUrl) {
        this.client = provider.http2(solrBaseUrl);
    }

    public <T> UpdateResponse index(String collectionName, T document) {
        try {
            client.addBean(collectionName, document);
            return client.commit(collectionName);
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
        return new UpdateResponse();
    }

    public <T> UpdateResponse indexCollection(String collectionName, Collection<T> documents) {
        try {
            client.addBeans(collectionName, documents);
            return client.commit(collectionName);
        } catch (IOException | SolrServerException e) {
            e.printStackTrace();
        }
        return new UpdateResponse();
    }

    public <T> UpdateResponse indexCollection(String collectionName, File json, Class<T> classToMap) {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, classToMap);
        Collection<T> documents = null;
        try {
            documents = mapper.readValue(json, collectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexCollection(collectionName, documents);
    }

    public UpdateResponse updateById(String collectionName, String id, String field, String newValue) {
        SolrDocument foundDoc = SolrFetchQuery.of(collectionName)
                .queryById(id)
                ._processAndGetFirstResult(client);

        SolrInputDocument newDoc = new SolrInputDocument();
        newDoc.addField(field, newValue);
        for (Map.Entry<String, Object> entry : foundDoc.entrySet()) {
            if (entry.getKey().equals(field) || entry.getKey().equals("_version_") || entry.getKey().equals("name_exact")) {
                continue;
            }
            newDoc.addField(entry.getKey(), entry.getValue());
        }

        return SolrUpdateRequest.of(collectionName)
                .add(newDoc)
                .setCommitTrue()
                .setOverwriteTrue()
                .setCommitWithin(1000)
                ._process(client);
    }

    public UpdateResponse updateById(String collectionName, String id, Map<String, Object> fields) {
        SolrDocument foundDoc = SolrFetchQuery.of(collectionName)
                .queryById(id)
                ._processAndGetFirstResult(client);

        SolrInputDocument newDoc = new SolrInputDocument();

        fields.forEach(newDoc::addField);
        foundDoc.forEach((key, value) -> {
            if (fields.containsKey(key) || key.equals("_version_") || key.equals("name_exact")) {
                return;
            }
            newDoc.addField(key, value);
        });

        return SolrUpdateRequest.of(collectionName)
                .add(newDoc)
                .setCommitTrue()
                .setOverwriteTrue()
                .setCommitWithin(1000)
                ._process(client);
    }

    public UpdateResponse deleteById(String collectionName, String id) {
        try {
            return client.deleteById(collectionName, id);
        } catch (IOException | SolrServerException e) {
            e.printStackTrace();
        }
        return new UpdateResponse();
    }

    public UpdateResponse deleteAll(String collectionName) {
        try {
            return client.deleteByQuery(collectionName, "*:*");
        } catch (IOException | SolrServerException e) {
            e.printStackTrace();
        }
        return new UpdateResponse();
    }
}
