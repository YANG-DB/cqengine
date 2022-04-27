package com.googlecode.cqengine.query;

import com.googlecode.cqengine.TypedIndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;

public interface JoinPlannerStrategy {
    boolean join(QueryJoinContext context, TypedIndexedCollection sideA, TypedIndexedCollection sideB);

    public static class TypedFKCollectionStrategy implements JoinPlannerStrategy {

        @Override
        public boolean join(QueryJoinContext context, TypedIndexedCollection sideA, TypedIndexedCollection sideB) {
            if (sideA instanceof TypedIndexedCollection.PKTypedIndexedCollection && sideB instanceof TypedIndexedCollection.FKTypedIndexedCollection) {
                Attribute fkAttribute = ((TypedIndexedCollection.FKTypedIndexedCollection) sideB).getFKAttributeByClass(sideA.getType());
                Attribute<?, ?> pkAttribute = ((TypedIndexedCollection.PKTypedIndexedCollection<?, ?>) sideA).getPKAttribute();
                context.append(sideB, QueryFactory.existsIn(sideB.getIndexedCollection(), pkAttribute, fkAttribute));
                return true;
            }
            return false;
        }
    }
}
