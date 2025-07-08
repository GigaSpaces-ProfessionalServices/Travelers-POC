#!/bin/bash

# -- CONFIGURE THIS --
KAFKA_UI_URL="http://travelers.labs.gigaspaces.net:8140"
TOPIC_NAME="dih-write-back"

# Get the cluster name (assumes only one)
CLUSTER_NAME=$(curl -s "$KAFKA_UI_URL/api/clusters" | jq -r '.[0].name')

if [ -z "$CLUSTER_NAME" ]; then
  echo "❌ Could not find any Kafka cluster from Kafka UI"
  exit 1
fi

echo "✅ Cluster: $CLUSTER_NAME"

# Get topic configuration
TOPIC_JSON=$(curl -s "$KAFKA_UI_URL/api/clusters/$CLUSTER_NAME/topics/$TOPIC_NAME")

if [ "$(echo "$TOPIC_JSON" | jq -r '.name')" == "null" ]; then
  echo "❌ Topic '$TOPIC_NAME' not found"
  exit 1
fi

PARTITIONS=$(echo "$TOPIC_JSON" | jq -r '.partitions | length')
REPLICATION=$(echo "$TOPIC_JSON" | jq -r '.partitions[0].replicas | length')

echo "📦 Found topic with $PARTITIONS partitions and replication factor $REPLICATION"

# Extract configs
CONFIGS=$(echo "$TOPIC_JSON" | jq -c '
  if .config == null then {} else .config end
')

echo "🔧 Topic configs: $CONFIGS"

# Delete topic
echo "🗑️ Deleting topic '$TOPIC_NAME'..."
curl -s -X DELETE "$KAFKA_UI_URL/api/clusters/$CLUSTER_NAME/topics/$TOPIC_NAME"
echo "⏳ Waiting for deletion..."
sleep 5

# Recreate topic
echo "🔁 Recreating topic '$TOPIC_NAME'..."
curl -s -X POST "$KAFKA_UI_URL/api/clusters/$CLUSTER_NAME/topics" \
  -H "Content-Type: application/json" \
  -d @- <<EOF
{
  "name": "$TOPIC_NAME",
  "partitions": $PARTITIONS,
  "replicationFactor": $REPLICATION,
  "configs": $CONFIGS
}
EOF

echo "✅ Topic '$TOPIC_NAME' recreated."
