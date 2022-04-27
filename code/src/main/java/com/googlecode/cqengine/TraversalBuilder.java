package com.googlecode.cqengine;

import com.googlecode.cqengine.TypedIndexedCollection.FKTypedIndexedCollection;
import com.googlecode.cqengine.TypedIndexedCollection.PKTypedIndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.metadata.ForeignKeyedEntity;
import com.googlecode.cqengine.metadata.PrimaryKeyedEntity;
import com.googlecode.cqengine.query.*;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.googlecode.cqengine.resultset.ResultSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.googlecode.cqengine.query.QueryFactory.all;
import static com.googlecode.cqengine.query.QueryFactory.and;

public class TraversalBuilder<O extends PrimaryKeyedEntity, FK extends ForeignKeyedEntity> {

    private QueryJoinContext context;
    private List<JoinPlannerStrategy> strategies;

    private TraversalBuilder() {

    }

    public static TraversalBuilder build(List<JoinPlannerStrategy> strategies) {
        TraversalBuilder<PrimaryKeyedEntity, ForeignKeyedEntity> traversalBuilder = new TraversalBuilder<>();
        traversalBuilder.strategies = strategies;
        traversalBuilder.context = new QueryJoinContext();
        return traversalBuilder;

    }

    public TraversalBuilder<O, FK> with(TypedIndexedCollection<O> collection) {
        context.append(collection);
        return this;
    }

    public TraversalBuilder<O, FK> with(TypedIndexedCollection<O> collection, Query<O> query) {
        context.append(collection, query);
        return this;
    }

    public TraversalBuilder<O, FK> joinWith(final TypedIndexedCollection foreignCollection, Query<O> foreignQuery, final Attribute localKeyAttribute, final Attribute foreignKeyAttribute) {
        context.append(foreignCollection,
                QueryFactory.existsIn(foreignCollection.getIndexedCollection(),
                        localKeyAttribute, foreignKeyAttribute));

        context.current().and(foreignQuery);
        return this;
    }

    public TraversalBuilder<O, FK> joinWith(final TypedIndexedCollection foreignCollection, final Attribute localKeyAttribute, final Attribute foreignKeyAttribute) {
        context.append(foreignCollection,
                QueryFactory.existsIn(foreignCollection.getIndexedCollection(),
                        localKeyAttribute, foreignKeyAttribute));
        return this;
    }

    public TraversalBuilder<O, FK> joinWith(final TypedIndexedCollection<O> foreignCollection) {
        for (JoinPlannerStrategy strategy : strategies) {
            if (strategy.join(context, context.current().index, foreignCollection)) {
                return this;
            }
        }
        //strategies are expected to have default join
        return this;
    }

    public TraversalBuilder<O, FK> joinWith(final TypedIndexedCollection<O> foreignCollection, Query<O> foreignQuery) {
        for (JoinPlannerStrategy strategy : strategies) {
            if (strategy.join(context, context.current().index, foreignCollection)) {
                context.current().and(foreignQuery);
                return this;
            }
        }
        //strategies are expected to have default join
        return this;
    }

    public Stream<TraversalPath<O>> retrieve() {
        return null;
    }

    public Map<Object, Map> retrieveAll() {
        Map<Object, Map> rootContext = new HashMap<>();
        Map<Object, Map> pathContext = rootContext;
        Object contextNode;
        //for every node in the pattern
        for (JoinTuple node : context.pattern) {
            // traverse in DFS manner - only surviving path that contain the full populated pattern remain
            ResultSet resultSet = node.retrieve();
            rootContext.putAll((Map<?, ? extends Map>) StreamSupport
                    .stream(resultSet.spliterator(), false)
                    .collect(Collectors.toMap(Function.identity(), v->new HashMap())));

        }
        return rootContext;

    }

    public TraversalPath<O> retrieve(QueryOptions queryOptions) {
//        Path<O> path = new TraversalPath<>();
        return null;
    }


    public static class TraversalPath<T> implements Path<T> {
        private T root;
        private List elements;

        public TraversalPath(T root, List elements) {
            this.root = root;
            this.elements = elements;
        }

        @Override
        public T getRoot() {
            return root;
        }

        @Override
        public List expand() {
            return elements;
        }

        @Override
        public Object getAt(int index) {
            return elements.get(index);
        }
    }


    public static class JoinTuple<T, C> {
        TypedIndexedCollection<T> index;
        Query<T> query;

        public JoinTuple(TypedIndexedCollection<T> index, Query<T> query) {
            this.index = index;
            this.query = query;
        }

        public JoinTuple(TypedIndexedCollection<T> index) {
            this(index, all(index.getType()));
        }

        public JoinTuple<T, C> and(Query<T> query) {
            this.query = QueryFactory.and(this.query, query);
            return this;
        }

        public ResultSet<T> retrieve() {
            return this.index.retrieve(query);
        }

        public ResultSet<T> retrieve(int batchSize) {
            return this.index.retrieve(query);
        }

        public ResultSet<T> retrieve(Object element) {
            return this.index.retrieve(query);
        }

    }

}
