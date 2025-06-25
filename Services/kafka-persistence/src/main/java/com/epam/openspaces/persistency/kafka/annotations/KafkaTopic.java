package com.epam.openspaces.persistency.kafka.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the Kafka topic for the annotated entity.
 *
 * @author Oleksiy_Dyagilev
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface KafkaTopic {
    String value();
}
