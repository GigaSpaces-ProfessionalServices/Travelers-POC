#!/bin/bash

source ./set-env.sh

echo "Deploying mirror service..."
$XAP_HOME/bin/gs.sh pu deploy kafka-pers-mirror ../mirror/target/kafka-pers-mirror.jar

echo "Deploying processor..."
$XAP_HOME/bin/gs.sh pu deploy --partitions=1 --backups=1 kafka-pers-processor ../processor/target/kafka-pers-processor.jar

echo "Deploying feeder..."
$XAP_HOME/bin/gs.sh pu deploy kafka-pers-feeder ../feeder/target/kafka-pers-feeder.jar

echo "Deploying consumer..."
$XAP_HOME/bin/gs.sh pu deploy kafka-pers-consumer ../consumer/target/kafka-pers-consumer.jar