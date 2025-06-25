package com.epam.common;

import com.epam.openspaces.persistency.kafka.annotations.KafkaTopic;
import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;

import java.io.Serializable;

/**
 * A simple object used to work with the Space.
 */
@KafkaTopic("person")
@SpaceClass
public class Person implements Serializable {
    private String id;

    private String name;

    private Integer age;

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
    public void setAge(Integer age) {
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
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
