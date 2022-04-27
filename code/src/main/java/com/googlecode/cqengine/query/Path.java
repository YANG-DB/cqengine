package com.googlecode.cqengine.query;

import java.util.List;

/**
 * this path is a traversal result set that creates a path (row) for every instance
 * of a pattern in the resulting join graph
 *
 * for example if we have  a->b1->c
 *                          ->b2->c
 *                          ->b2->d
 *
 *  will result with:
 *
 *  a->b1->c
 *  a->b2->c
 *  a->b2->d
 *
 * @param <T>
 */
public interface Path<T> {
    /**
     * get root of the path
     * @return
     */
    T getRoot();

    /**
     * expand all the path
     * @return
     */
    List expand();

    /**
     * get a specific element of the path in a given location
     * @param index
     * @return
     */
    Object getAt(int index);

}
