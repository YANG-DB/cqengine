package com.googlecode.cqengine.examples.introduction;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.metadata.ForeignKeyedEntity;
import com.googlecode.cqengine.metadata.ForeignKeyedMetadata;
import com.googlecode.cqengine.query.option.QueryOptions;

public class CarsRegistry implements ForeignKeyedEntity {
    private int carId;
    private String personId;
    private long purchaseDate;

    public CarsRegistry(int carId, String personId, long purchaseDate) {
        this.carId = carId;
        this.personId = personId;
        this.purchaseDate = purchaseDate;
    }

    public static final Attribute<CarsRegistry, Integer> CAR_ID = new SimpleAttribute<CarsRegistry, Integer>() {
        public Integer getValue(CarsRegistry registry, QueryOptions queryOptions) {
            return registry.carId;
        }
    };

    public static final Attribute<CarsRegistry, String> PERSON_ID = new SimpleAttribute<CarsRegistry, String>() {
        public String getValue(CarsRegistry registry, QueryOptions queryOptions) {
            return registry.personId;
        }
    };

    public static final Attribute<CarsRegistry, Long> PURCHASE_DATE = new SimpleAttribute<CarsRegistry, Long>() {
        public Long getValue(CarsRegistry registry, QueryOptions queryOptions) {
            return registry.purchaseDate;
        }
    };


    @Override
    public Object getForeignKeyByClass(Class type) {
        if (Person.class.equals(type)) {
            return personId;
        }

        if (Car.class.equals(type)) {
            return personId;
        }

        return null;
    }

    public static class CarsRegistryMetadata implements ForeignKeyedMetadata {

        @Override
        public Attribute getFKAttributeByClass(Class type) {
            if (Person.class.equals(type)) {
                return PERSON_ID;
            }

            if (Car.class.equals(type)) {
                return CAR_ID;
            }

            return null;
        }


    }
}
