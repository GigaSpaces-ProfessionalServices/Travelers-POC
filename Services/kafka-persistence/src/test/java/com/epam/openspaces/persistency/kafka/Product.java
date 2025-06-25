package com.epam.openspaces.persistency.kafka;

import com.gigaspaces.document.SpaceDocument;

import static com.epam.openspaces.persistency.kafka.KafkaPersistenceConstants.SPACE_DOCUMENT_KAFKA_TOPIC_PROPERTY_NAME;

/**
 * @author Mykola_Zalyayev
 */
public class Product extends SpaceDocument {
    private static final String TYPE_NAME = "Product";
    private static final String PROPERTY_CATALOG_NUMBER = "CatalogNumber";
    private static final String PROPERTY_NAME = "Name";
    private static final String PROPERTY_PRICE = "Price";

    public Product() {
        super(TYPE_NAME);
        super.setProperty(SPACE_DOCUMENT_KAFKA_TOPIC_PROPERTY_NAME, "Product");
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
