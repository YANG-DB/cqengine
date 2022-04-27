package com.googlecode.cqengine.examples.introduction;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.MultiValueAttribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.metadata.PrimaryKeyedEntity;
import com.googlecode.cqengine.metadata.PrimaryKeyedMetadata;
import com.googlecode.cqengine.query.option.QueryOptions;

import java.util.List;

public class Person implements PrimaryKeyedEntity<String> {
    public final String Id;
    public final int age;
    public final String name;
    public final String lastName;
    public final List<String> addresses;


    public Person(String id, int age, String name, String lastName, List<String> addresses) {
        this.Id = id;
        this.age = age;
        this.name = name;
        this.lastName = lastName;
        this.addresses = addresses;
    }

    // -------------------------- Attributes --------------------------
    public static final Attribute<Person, String> ID = new SimpleAttribute<Person, String>("Id") {
        public String getValue(Person person, QueryOptions queryOptions) {
            return person.Id;
        }
    };
    public static final Attribute<Person, Integer> AGE = new SimpleAttribute<Person, Integer>("Age") {
        public Integer getValue(Person person, QueryOptions queryOptions) {
            return person.age;
        }
    };

    public static final Attribute<Person, String> NAME = new SimpleAttribute<Person, String>("name") {
        public String getValue(Person person, QueryOptions queryOptions) {
            return person.name;
        }
    };
    public static final Attribute<Person, String> LAST_NAME = new SimpleAttribute<Person, String>("lastName") {
        public String getValue(Person person, QueryOptions queryOptions) {
            return person.lastName;
        }
    };

    public static final Attribute<Person, String> ADDRESSES = new MultiValueAttribute<Person, String>("addresses") {
        public List<String> getValues(Person person, QueryOptions queryOptions) {
            return person.addresses;
        }
    };

    @Override
    public String getKey() {
        return this.Id;
    }


    public static final class PersonMetadata implements PrimaryKeyedMetadata<Person, String> {

        @Override
        public Attribute<Person, String> getPKAttribute() {
            return ID;
        }
    }
}
