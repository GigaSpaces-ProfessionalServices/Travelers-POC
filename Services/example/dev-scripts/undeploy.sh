#!/bin/bash

source ./set-env.sh

echo "Undeploying ... "
$XAP_HOME/bin/gs.sh pu undeploy kafka-pers-feeder

$XAP_HOME/bin/gs.sh pu undeploy kafka-pers-consumer

$XAP_HOME/bin/gs.sh pu undeploy kafka-pers-processor

$XAP_HOME/bin/gs.sh pu undeploy kafka-pers-mirror