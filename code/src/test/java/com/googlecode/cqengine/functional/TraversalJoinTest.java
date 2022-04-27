/**
 * Copyright 2012-2015 Niall Gallagher
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.cqengine.functional;

import com.googlecode.cqengine.*;
import com.googlecode.cqengine.TypedIndexedCollection.FKTypedIndexedCollection;
import com.googlecode.cqengine.TypedIndexedCollection.PKTypedIndexedCollection;
import com.googlecode.cqengine.examples.introduction.Car;
import com.googlecode.cqengine.examples.introduction.CarsRegistry.CarsRegistryMetadata;
import com.googlecode.cqengine.examples.introduction.Person;
import com.googlecode.cqengine.examples.introduction.CarsRegistry;
import com.googlecode.cqengine.examples.introduction.Garage;
import com.googlecode.cqengine.examples.introduction.Person.PersonMetadata;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.logical.And;
import com.googlecode.cqengine.resultset.ResultSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static com.googlecode.cqengine.TraversalBuilder.*;
import static com.googlecode.cqengine.query.QueryFactory.*;
import static com.googlecode.cqengine.TypedIndexedCollection.typed;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Niall Gallagher
 */
public class TraversalJoinTest {

    // Create an indexed collection of people...
    TypedIndexedCollection<Person> people = typed(Person.class, new PersonMetadata());
    Person man1 = new Person("1111111", 34, "Brown", "Bobi", asList("here", "there"));
    Person man2 = new Person("1111112", 54, "Blue", "Manni", asList("here", "there"));
    Person man3 = new Person("1111113", 24, "Green", "Didi", asList("here", "there"));

    {
        people.addAll(asList(man1, man2, man3));
    }

    // Create an indexed collection of car ownership...
    TypedIndexedCollection<CarsRegistry> ownership = typed(CarsRegistry.class, new CarsRegistryMetadata());
    CarsRegistry own1 = new CarsRegistry(1, "1111111", new Date().getTime());
    CarsRegistry own2 = new CarsRegistry(2, "1111111", new Date().getTime() - 1000);
    CarsRegistry own3 = new CarsRegistry(3, "1111112", new Date().getTime() - 4000);
    CarsRegistry own4 = new CarsRegistry(4, "1111111", new Date().getTime() - 450000);
    CarsRegistry own5 = new CarsRegistry(5, "1111113", new Date().getTime() + 10000);

    {
        ownership.addAll(asList(own1, own2, own3, own4, own5));
    }


    // Create an indexed collection of cars...
    TypedIndexedCollection<Car> cars = typed(Car.class, new ConcurrentIndexedCollection<>());
    Car car1 = new Car(1, "Ford Focus", "great condition, low mileage", asList("spare tyre", "sunroof"));
    Car car2 = new Car(2, "Ford Taurus", "dirty and unreliable, flat tyre", asList("spare tyre", "radio"));
    Car car3 = new Car(3, "Honda Civic", "has a flat tyre and high mileage", asList("radio"));
    Car car4 = new Car(4, "BMW M3", "2013 model", asList("radio", "convertible"));
    Car car5 = new Car(5, "BMW M6", "2014 model", asList("radio", "convertible"));

    {
        cars.addAll(asList(car1, car2, car3, car4, car5));
    }

    // Create an indexed collection of garages...
    final TypedIndexedCollection<Garage> garages = typed(Garage.class, new ConcurrentIndexedCollection<>());
    Garage garage1 = new Garage(1, "Joe's garage", "London", asList("Ford Focus", "Honda Civic"));
    Garage garage2 = new Garage(2, "Jane's garage", "Dublin", asList("BMW M3"));
    Garage garage3 = new Garage(3, "John's garage", "Dublin", asList("Ford Focus", "Ford Taurus"));
    Garage garage4 = new Garage(4, "Jill's garage", "Dublin", asList("Ford Focus"));
    Garage garage5 = new Garage(5, "Sam's garage", "Dubai", asList("BMW M3", "BMW M6"));
    Garage garage6 = new Garage(6, "Jen's garage", "Galway", asList("Bat Mobile", "Golf Cart"));

    {
        garages.addAll(asList(garage1, garage2, garage3, garage4, garage5, garage6));
    }


    @Test
    public void testTraversalSingleStepJoin() {
        Map all = build(new ArrayList<>())
                .with(people)
                .retrieveAll();
        assertEquals(3, all.size());
    }

    @Test
    public void testTraversalSingleStepJoinWithQuery() {
        Map filtered = build(new ArrayList<>())
                .with(people, greaterThan(Person.AGE, 49))
                .retrieveAll();
        assertEquals(1, filtered.size());
    }

    @Test
    public void testTraversalMultiStepsJoin() {

        Map all = build(new ArrayList<>())
                .with(people, greaterThan(Person.AGE, 49))
                .joinWith(ownership)
//                .joinWith(ownership, Person.ID, CarsRegistry.PERSON_ID)._()
                .joinWith(cars, startsWith(Car.NAME, "ford"))
//                .joinWith(cars, CarsRegistry.CAR_ID, Car.CAR_ID)
                .joinWith(garages, equal(Garage.LOCATION, "Dublin"),
                        Car.NAME,
                        Garage.BRANDS_SERVICED)
                .retrieveAll();

        assertEquals("elements in traversal", 5, all.size());

/*
        And<Person> person = and(
                greaterThan(Person.AGE, 49),
                existsIn(ownership,
                        Person.ID,
                        CarsRegistry.PERSON_ID
                )
        );

        ResultSet<Person> resultSet = typed(Person.class, people).getIndexedCollection().retrieve(person);
        assertEquals(1, resultSet.size());
        assertEquals(man2, resultSet.uniqueResult());

        And<CarsRegistry> ownedCars = and(
                all(CarsRegistry.class),
                existsIn(cars,
                        CarsRegistry.CAR_ID,
                        Car.CAR_ID
                )
        );

        ResultSet<CarsRegistry> ownerships = typed(CarsRegistry.class, ownership).getIndexedCollection().retrieve(ownedCars);
        assertEquals(5, ownerships.size());

        And<Car> carHandledInGarage = and(
                in(Car.FEATURES, "sunroof", "convertible"),
                existsIn(garages,
                        Car.NAME,
                        Garage.BRANDS_SERVICED,
                        equal(Garage.LOCATION, "Dublin")
                )
        );


        ResultSet<Car> carsResults = typed(Car.class, cars).getIndexedCollection().retrieve(carHandledInGarage);
        assertEquals(2, carsResults.size());


        ResultSet<Garage> garagesList = typed(Garage.class, garages).getIndexedCollection().retrieve(all(Garage.class));
        assertEquals(6, garagesList.size());

        //  Cypher matching query   `Match (p:Person)->[owns:Ownership]->(c:Car{ })->[g:Garage { location:"Dublin"}]`
        GraphIndexedCollectionsUnionWrapper graph = new GraphIndexedCollectionsUnionWrapper(
                typed(Person.class, people),
                typed(Car.class, cars),
                typed(CarsRegistry.class, ownership),
                typed(Garage.class, garages));

        And traverse = traverse(
                person,
                ownedCars,
                carHandledInGarage,
                all(Garage.class)
        );

        ResultSet path = graph.retrieve(traverse);

        Map<Car, Set<Garage>> results = new LinkedHashMap<Car, Set<Garage>>();
        for (Car car : cars.retrieve(carsQuery)) {
            Query<Garage> garagesWhichServiceThisCarInDublin
                    = and(equal(Garage.BRANDS_SERVICED, car.name), equal(Garage.LOCATION, "Dublin"));
            for (Garage garage : garages.retrieve(garagesWhichServiceThisCarInDublin)) {
                Set<Garage> garagesWhichCanServiceThisCar = results.get(car);
                if (garagesWhichCanServiceThisCar == null) {
                    garagesWhichCanServiceThisCar = new LinkedHashSet<Garage>();
                    results.put(car, garagesWhichCanServiceThisCar);
                }
                garagesWhichCanServiceThisCar.add(garage);
            }
        }

        assertEquals("join results should contain 2 cars", 2, results.size());
        Assert.assertTrue("join results should contain car1", results.containsKey(car1));
        Assert.assertTrue("join results should contain car4", results.containsKey(car4));

        assertEquals("join results for car1", asSet(garage3, garage4), results.get(car1));
        assertEquals("join results for car4", asSet(garage2), results.get(car4));
*/
    }


    static <T> Set<T> asSet(T... objects) {
        return asSet(asList(objects));
    }

    static <T> Set<T> asSet(Iterable<T> objects) {
        Set<T> results = new LinkedHashSet<T>();
        for (T object : objects) {
            results.add(object);
        }
        return results;
    }
}
