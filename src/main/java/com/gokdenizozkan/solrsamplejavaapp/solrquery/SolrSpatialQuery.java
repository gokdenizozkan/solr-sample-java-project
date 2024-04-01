package com.gokdenizozkan.solrsamplejavaapp.solrquery;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SpatialParams;
import org.apache.solr.common.params.StatsParams;

import java.util.Arrays;
import java.util.List;

public class SolrSpatialQuery {
    private final String collectionName;
    private final SolrQuery query;

    private SolrSpatialQuery(String collectionName) {
        this.collectionName = collectionName;
        this.query = new SolrQuery();
        query.set("solrquery", true);
    }

    public static SolrSpatialQuery of(String collectionName) {
        return new SolrSpatialQuery(collectionName);
    }

    private static String stringifyFields(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(clazz.getDeclaredFields())
                .forEach(field -> sb.append(field.getName()).append(","));
        return sb.toString();
    }

    public SolrSpatialQuery query(String query) {
        this.query.setQuery(query);
        return this;
    }

    public SolrSpatialQuery point(String point) {
        query.set(SpatialParams.POINT, point);
        return this;
    }

    public SolrSpatialQuery latlonFieldName(String latlonFieldName) {
        query.set(SpatialParams.FIELD, latlonFieldName);
        return this;
    }

    public SolrSpatialQuery distance(int km) {
        query.set(SpatialParams.DISTANCE, km);
        return this;
    }

    public SolrSpatialQuery rows(Integer rows) {
        if (rows <= 0) {
            return this;
        }
        query.set("rows", rows);
        return this;
    }

    public SolrSpatialQuery filter(FilterType filterType) {
        query.setFilterQueries(filterType.toString());
        return this;
    }

    public SolrSpatialQuery sort(QueryFunction queryFunction, SortOrder sortOrder) {
        query.set("sort", String.format("%s %s", queryFunction, sortOrder));
        return this;
    }

    public SolrSpatialQuery fieldList(String fieldList) {
        query.setFields(fieldList);
        return this;
    }

    /**
     * Instead of typing field names manually, this method can be used to stringify fields of a class.<br>
     * To utilize this method, make sure your collection/core schema has related fields defined.
     * @param clazz the class to stringify its fields
     */
    public SolrSpatialQuery fieldList(Class<?> clazz) {
        String fieldList = stringifyFields(clazz);
        query.setFields(fieldList);
        return this;
    }

    /**
     * Adds stats to the query.<br>
     * Sets "stats" parameter to true if called.<br>
     * @param statFields fields to set for stats
     */
    public SolrSpatialQuery stats(String... statFields) {
        query.set(StatsParams.STATS, true);
        Arrays.stream(statFields).forEach(field -> query.add(StatsParams.STATS_FIELD, field));
        return this;
    }

    public QueryResponse _process(SolrClient client) {
        QueryResponse response = null;
        try {
            response = client.query(collectionName, query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public <T> List<T> _processAndGetAllAs(SolrClient client, Class<T> convertToClass) {
        QueryResponse response = _process(client);
        return response.getBeans(convertToClass);
    }

    public SolrQuery toQuery() {
        return query;
    }

    public String toQueryString() {
        return query.toQueryString();
    }

    public enum FilterType {
        GEOFILT("{!geofilt}"); // BBOX(""), NO_FILTER("");

        private final String value;

        FilterType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum QueryFunction {
        GEODIST("{!func}geodist()"); //DIST, HSIN, SQEDIST;

        private final String value;

        QueryFunction(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum SortOrder {
        ASC, DESC;

        @Override
        public String toString() {
            return super.name().toLowerCase();
        }
    }
}
