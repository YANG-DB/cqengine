package com.googlecode.cqengine.query;

import com.googlecode.cqengine.TraversalBuilder;
import com.googlecode.cqengine.TypedIndexedCollection;
import com.googlecode.cqengine.metadata.PrimaryKeyedEntity;

import java.util.ArrayList;
import java.util.List;

public class QueryJoinContext {
    public int batchSize = 10000;
    public List<TraversalBuilder.JoinTuple<?, QueryJoinContext>> pattern = new ArrayList<>();

    public QueryJoinContext append(TypedIndexedCollection collection) {
        pattern.add(new TraversalBuilder.JoinTuple(collection));
        return this;
    }

    public QueryJoinContext append(TypedIndexedCollection collection, Query query) {
        pattern.add(new TraversalBuilder.JoinTuple(collection, query));
        return this;
    }

    public <O extends PrimaryKeyedEntity> TraversalBuilder.JoinTuple current() {
        return pattern.get(pattern.size() - 1);
    }
}
