package com.epam.common;

import com.gigaspaces.document.SpaceDocument;

import static com.epam.openspaces.persistency.kafka.KafkaPersistenceConstants.SPACE_DOCUMENT_KAFKA_TOPIC_PROPERTY_NAME;

/**
 * A simple extended space document used to work with the Space.
 * Demonstrates how to set topic name which kafka producer write into.
 */
public class Product extends SpaceDocument {
    private static final String TYPE_NAME = "Product";
    private static final String PROPERTY_CATALOG_NUMBER = "CatalogNumber";
    private static final String PROPERTY_NAME = "Name";
    private static final String PROPERTY_PRICE = "Price";

    /*
     * Set property to SpaceDocument topic name. Default property name is "spaceDocument.kafka.topic"
     */
    public Product() {
        super(TYPE_NAME);
        super.setProperty(SPACE_DOCUMENT_KAFKA_TOPIC_PROPERTY_NAME, "product");
    }

    public String getCatalogNumber() {
        return super.getProperty(PROPERTY_CATALOG_NUMBER);
    }
    public Product setCatalogNumber(String catalogNumber) {
        super.setProperty(PROPERTY_CATALOG_NUMBER, catalogNumber);
        return this;
    }

    public String getName() {
        return super.getProperty(PROPERTY_NAME);
    }
    public Product setName(String name) {
        super.setProperty(PROPERTY_NAME, name);
        return this;
    }

    public Float getPrice() {
        return super.getProperty(PROPERTY_PRICE);
    }
    public Product setPrice(float price) {
        super.setProperty(PROPERTY_PRICE, price);
        return this;
    }
}
