package com.epam.openspaces.persistency.kafka;

/**
 * Indicates a problem during persisting data to Kafka
 *
 * @author Oleksiy_Dyagilev
 */
public class KafkaPersistenceException extends Exception {

    public KafkaPersistenceException() {
        super();
    }

    public KafkaPersistenceException(String message) {
        super(message);
    }

    public KafkaPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaPersistenceException(Throwable cause) {
        super(cause);
    }
}
