package com.googlecode.cqengine;

import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.googlecode.cqengine.resultset.ResultSet;

public interface IndexedCollectionsUnion {


    /**
     * Shortcut for calling {@link #retrieve(Query, QueryOptions)} without supplying any query options.
     */
    ResultSet retrieve(Query query);


    ResultSet retrieve(Query query, QueryOptions queryOptions);


}
