package com.googlecode.cqengine;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.metadata.ForeignKeyedMetadata;
import com.googlecode.cqengine.metadata.PrimaryKeyedMetadata;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.googlecode.cqengine.resultset.ResultSet;

import java.util.List;

public class TypedIndexedCollection<O> {

    private Class<O> type;
    private IndexedCollection<O> indexedCollection;

    public static <O, PK> PKTypedIndexedCollection<O, PK> typed(Class<O> type, PrimaryKeyedMetadata<O, PK> metadata) {
        return new PKTypedIndexedCollection<>(type, metadata, new ConcurrentIndexedCollection<>());
    }

    public static <O, PK> PKTypedIndexedCollection<O, PK> typed(Class<O> type, PrimaryKeyedMetadata<O, PK> metadata, IndexedCollection<O> indexedCollection) {
        return new PKTypedIndexedCollection<>(type, metadata, indexedCollection);
    }
    public static <O> FKTypedIndexedCollection<O> typed(Class<O> type, ForeignKeyedMetadata metadata, IndexedCollection<O> indexedCollection) {
        return new FKTypedIndexedCollection<>(type, metadata, indexedCollection);
    }

    public static <O> FKTypedIndexedCollection<O> typed(Class<O> type, ForeignKeyedMetadata metadata) {
        return new FKTypedIndexedCollection<>(type, metadata, new ConcurrentIndexedCollection<>());
    }

    public static <O> TypedIndexedCollection<O> typed(Class<O> type, IndexedCollection<O> index) {
        return new TypedIndexedCollection<>(type, index);
    }

    public TypedIndexedCollection(Class<O> type, IndexedCollection<O> indexedCollection) {
        this.type = type;
        this.indexedCollection = indexedCollection;
    }

    public Class<O> getType() {
        return type;
    }

    /**
     * Shortcut for calling {@link #retrieve(Query, QueryOptions)} without supplying any query options.
     */
    public ResultSet<O> retrieve(Query<O> query) {
        return this.indexedCollection.retrieve(query);
    }

    /**
     * {@inheritDoc}
     */
    public ResultSet<O> retrieve(Query<O> query, QueryOptions queryOptions) {
        return this.indexedCollection.retrieve(query,queryOptions);

    }

    public IndexedCollection<O> getIndexedCollection() {
        return indexedCollection;
    }

    public void addAll(List<O> list) {
        this.indexedCollection.addAll(list);
    }

    public void add(O element) {
        this.indexedCollection.add(element);
    }

    public static class PKTypedIndexedCollection<O, PK> extends TypedIndexedCollection<O> implements PrimaryKeyedMetadata<O, PK> {

        private PrimaryKeyedMetadata<O, PK> metadata;

        public PKTypedIndexedCollection(Class<O> type, PrimaryKeyedMetadata<O, PK> metadata, IndexedCollection<O> indexedCollection) {
            super(type, indexedCollection);
            this.metadata = metadata;
        }

        @Override
        public Attribute<O, PK> getPKAttribute() {
            return metadata.getPKAttribute();
        }
    }

    public static class FKTypedIndexedCollection<O> extends TypedIndexedCollection<O> implements ForeignKeyedMetadata {

        private ForeignKeyedMetadata metadata;

        public FKTypedIndexedCollection(Class<O> type, ForeignKeyedMetadata metadata, IndexedCollection<O> indexedCollection) {
            super(type, indexedCollection);
            this.metadata = metadata;
        }

        @Override
        public Attribute getFKAttributeByClass(Class type) {
            return metadata.getFKAttributeByClass(type);
        }
    }
}

