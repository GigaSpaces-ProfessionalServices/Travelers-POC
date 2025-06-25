package com.epam.openspaces.persistency.kafka;

import com.epam.openspaces.persistency.kafka.annotations.KafkaTopic;
import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;

import java.io.Serializable;
import java.util.Date;

/**
 * A simple object used to work with the Space.
 */
@KafkaTopic("data")
@SpaceClass
public class Person implements Serializable {

    private String id;

    private Integer age;

    private String name;

    /**
     * Constructs a new Person object.
     */
    public Person() {
    }

    /**
     * Constructs a new Person object with the given type
     * and raw data.
     */
    public Person(Integer age, String name) {
        this.age = age;
        this.name = name;
    }

    /**
     * The id of this object.
     */
    @SpaceId(autoGenerate = true)
    public String getId() {
        return id;
    }

    /**
     * The id of this object. Its value will be auto generated when it is written
     * to the space.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * The type of the data object. Used as the routing field when working with
     * a partitioned space.
     */
    @SpaceRouting
    public Integer getAge() {
        return age;
    }

    /**
     * The type of the data object. Used as the routing field when working with
     * a partitioned space.
     */
    public void setAge(Integer date) {
        this.age = age;
    }

    /**
     * The raw data this object holds.
     */
    public String getName() {
        return name;
    }

    /**
     * The raw data this object holds.
     */
    public void setName(String rawData) {
        this.name = rawData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (!id.equals(person.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
