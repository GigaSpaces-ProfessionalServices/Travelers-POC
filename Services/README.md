# GigaSpaces XAP and Kafka integration

## Introduction

Apache Kafka is a high-throughput distributed messaging system.
This project is aimed to integrate GigaSpaces with Apache Kafka so that GigaSpaces persists data operations to Kafka. Kafka is used in a sense of external data store. The data is Kafka in available for subscription for a range of use cases including loading into Hadoop or offline data warehousing systems for offline reporting and processing.

![Screenshot](./Pictures/Picture1.png)

Kafka persistence is essentially an implementation of SpaceSynchronizationEndpoint. It takes a batch of data sync operations, converts them to a custom message protocol and sends to Kafka server using Kafka Producer API.

GigaSpace-Kafka protocol is simple and represents data operation. The message consists of operation type and data. Data itself could be represented either as a single object or as a dictionary of key/values. Object corresponds to Space entity and dictionary to SpaceDocument. Since message should be sent over the wire, they should be serialized to bytes in some way. The default encoder utilizes Java serialization mechanism which implies Space classes (domain model) be serializable. 

By default Kafka messages are uniformly distributed among Kafka partitions. Please note, even though data sync operations appear ordered in SpaceSynchronizationEndpoint, it doesn’t imply correct ordering of data processing in Kafka consumers. Please see the picture below for details:


![Screenshot](./Pictures/Picture2.png)

## Getting started

There is an example application located in <project_root>/example which demonstrates how to configure Kafka persistence and implement a simple Kafka consumer.

In order to run an example, please follow the instruction below:

## Prerequisite
- Installation of Confluent ( you can get it from from here: https://www.confluent.io/installation/ )  
- [Installation of Gigaspaces v16.2.1](https://docs.gigaspaces.com/latest/started/installation.html?Highlight=downloading)
- Git, Maven and JDK 1.8


#### 1. Run insightedge/xap<br>
        ./gs.sh host run-agent --auto --gsc=5

#### 2  Edit <confluent-home>/etc/kafka/zookeeper.properties       
  Gigaspace ZK is running on port 2181
 
  Edit <confluent-home>/etc/kafka/zookeeper.properties
        
  and modify the Confluent ZK port from 2181 to 2182 (could be any unused port)     

#### 3. Start ZK
        cd <confluent-home>/bin
        ./zookeeper-server-start ../etc/kafka/zookeeper.properties
        
#### 4.Edit <confluent-home>/etc/kafka/server.properties
   
   modify ZK port from 2181 to 2182        
        
#### 5.	Start Kafka server <br>
        cd <confluent-home>/bin
        ./kafka-server-start ../etc/kafka/server.properties 
        
Note : If bind exception occurred on port 8090 while running kafka, Edit server.propeties as below :
        Uncomment below lines in /etc/kafka/server.properties and change port from 8090 to 8091 like below
        
        confluent.metadata.server.listeners=http://0.0.0.0:8091
        confluent.metadata.server.advertised.listeners=http://127.0.0.1:8091
        
#### 6. initials environment variables:<br>
        cd <project_root>/example/dev-scripts
        edit set-env.sh
        and put the right GROUP, LOCATORS and XAP_HOME (point to the right Gigaspaces version). 
#### 7.	Build project <br>
        ./rebuild.sh
#### 8.	Deploy example to GigaSpaces <br>
        ./deploy.sh
#### 9.	Check GigaSpaces log files, there should be messages printed by Feeder and Consumer.<br>

## Tear Down
    ./undeploy.sh

## Configuration

### Library dependency

The following maven dependency needs to be included to your project in order to use Kafka persistence. This artifact is built from <project_rood>/kafka-persistence source directory.

![Screenshot](./Pictures/Picture3.png)

### Mirror service 

Here is an example of Kafka Space Synchronization Endpoint configuration.

![Screenshot](./Pictures/Picture4.png)

Please consult [Kafka documentation](https://kafka.apache.org/documentation.html#producerconfigs) for the full list of available producer properties.
The default properties applied to Kafka producer are the following:

![Screenshot](./Pictures/Picture5.png)

These default properties could be overridden if there is a need to customize GigaSpace-Kafka protocol. See Customization section below for details.

### Space class

In order to associate Kafka topic with domain model class, class should be annotated with @KafkaTopic annotation and marked as Serializable. Here is an example

![Screenshot](./Pictures/Picture6.png)

### Space documents

To configure Kafka topic for SpaceDocuments or Extended SpaceDocument, the property KafkaPersistenceConstants.SPACE_DOCUMENT_KAFKA_TOPIC_PROPERTY_NAME should be added to document. Here is an example

![Screenshot](./Pictures/Picture7.png)

It’s also possible to configure name of the property which defines the Kafka topic for SpaceDocuments. Set spaceDocumentKafkaTopicName to the desired value as shown below.

![Screenshot](./Pictures/Picture8.png)

### Kafka consumers

Kafka persistence library provides a wrapper around native Kafka Consumer API to preset configuration responsible for GigaSpace-Kafka protocol serialization. Please see com.epam.openspaces.persistency.kafka.consumer.KafkaConsumer, example of how to use it could be found in <project_root>/example module.

## Customization

Kafka persistence was designed to be extensible and customizable. 

If you need to create a custom protocol between GigaSpace and Kafka, provide an implementation of AbstractKafkaMessage, AbstractKafkaMessageKey, AbstractKafkaMessageFactory.

If you’d like to customize how data sync operations are sent to Kafka or how Kafka topic is chosen for given entity, provide an implement of AbstractKafkaSpaceSynchronizationEndpoint.

If you want to create a custom serializer, look at KafkaMessageDecoder and KafkaMessageKeyDecoder.

Kafka Producer client (which is used under the hood) could be configured with a number of settings, see Kafka documentation.
