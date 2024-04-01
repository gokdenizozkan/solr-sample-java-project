package com.gokdenizozkan.solrsamplejavaapp.engine;

import com.gokdenizozkan.solrsamplejavaapp.config.SolrClientProvider;
import com.gokdenizozkan.solrsamplejavaapp.solrquery.SolrSpatialQuery;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

public class RadarEngine {
    private final SolrClient client;

    public RadarEngine(SolrClientProvider provider, String solrBaseUrl) {
        this.client = provider.http2(solrBaseUrl);
    }
    
    public QueryResponse findWithinRadius(String collectionName, String query, String point, String latlonFieldName, Integer km,
                                           SolrSpatialQuery.SortOrder sortOrder, String fieldList, Integer rows) {
        return SolrSpatialQuery.of(collectionName)
                .query(query)
                .point(point)
                .distance(km)
                .latlonFieldName(latlonFieldName)
                .filter(SolrSpatialQuery.FilterType.GEOFILT)
                .sort(SolrSpatialQuery.QueryFunction.GEODIST, sortOrder)
                .fieldList(fieldList)
                .rows(rows)
                ._process(client);
    }

    /**
     * Find within radius with stats.<br>
     * Stats are currently implemented for distance only.<br>
     * @param collectionName the name of the collection
     * @param query the query
     * @param point the point to calculate distance from
     * @param latlonFieldName name of the field that contains the comma-separated latlon values.
     * @param km the distance in kilometers
     * @param sortOrder the sort order
     * @param fieldList the field list of the response object
     * @param rows the number of rows to return
     * @return the query response
     */
    public QueryResponse findWithinRadiusWithStats(String collectionName, String query, String point, String latlonFieldName, Integer km,
                                                    SolrSpatialQuery.SortOrder sortOrder, String fieldList, Integer rows) {

        return SolrSpatialQuery.of(collectionName)
                .query(query)
                .point(point)
                .distance(km)
                .latlonFieldName(latlonFieldName)
                .filter(SolrSpatialQuery.FilterType.GEOFILT)
                .sort(SolrSpatialQuery.QueryFunction.GEODIST, sortOrder)
                .fieldList(fieldList)
                .rows(rows)
                .stats(SolrSpatialQuery.QueryFunction.GEODIST.toString())
                ._process(client);
    }
}