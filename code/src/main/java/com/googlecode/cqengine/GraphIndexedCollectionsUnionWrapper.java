package com.googlecode.cqengine;

import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.googlecode.cqengine.resultset.ResultSet;

import java.util.HashMap;
import java.util.Map;

public class GraphIndexedCollectionsUnionWrapper implements IndexedCollectionsUnion {

    private TypedIndexedCollection[] collections;

    public GraphIndexedCollectionsUnionWrapper(TypedIndexedCollection... collections) {
        this.collections = collections;
    }

    @Override
    public ResultSet retrieve(Query query) {
        return this.retrieve(query,null);
    }

    @Override
    public ResultSet retrieve(Query query, QueryOptions queryOptions) {
        Map<Class<?>, ResultSet> mapOfResults = new HashMap<>();
        for (int i = 0; i < collections.length; i++) {
            mapOfResults.put(collections[i].getType(), collections[i].getIndexedCollection().retrieve(query,queryOptions));
        }
        return null;
    }
}

