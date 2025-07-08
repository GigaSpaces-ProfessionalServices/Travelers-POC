#!/bin/bash

# --- CONFIG ---
API_HOST="http://travelers.labs.gigaspaces.net:6080"
PIPELINE_LIST_URL="$API_HOST/api/v1/pipeline/"
DELETE_URL_BASE="$API_HOST/api/v1/pipeline"
TMP_FILE="pipelines_raw.json"
SPACE_NAME="space"

# --- Step 1: Get all pipelines ---
echo "📥 Fetching pipelines from $PIPELINE_LIST_URL..."
curl -s -X GET "$PIPELINE_LIST_URL" -H 'accept: */*' > "$TMP_FILE"

if [ ! -s "$TMP_FILE" ]; then
  echo "❌ Failed to fetch pipelines or response is empty"
  exit 1
fi

# --- Step 2: Process pipelines for given space ---
echo "🔎 Looking for pipelines with spaceName == '$SPACE_NAME'..."

grep -o "{\"[^\}]*\"spaceName\":\"$SPACE_NAME\"[^\}]*}" "$TMP_FILE" | while read -r line; do
  pipelineId=$(echo "$line" | sed -n 's/.*"pipelineId":"\([^"]*\)".*/\1/p')
  
  if [ -n "$pipelineId" ]; then
    echo "📡 Checking status of pipeline: $pipelineId"

    STATUS_RESPONSE=$(curl -s -X GET "$API_HOST/api/v1/pipeline/$pipelineId/status" -H 'accept: */*')
    STATUS=$(echo "$STATUS_RESPONSE" | grep -o '"status":"[^"]*"' | head -n1 | cut -d':' -f2 | tr -d '"')

    echo "🔄 Current status: $STATUS"

    if [ "$STATUS" == "RUNNING" ]; then
      echo "🛑 Stopping pipeline: $pipelineId"
      curl -s -X POST "$API_HOST/api/v1/pipeline/$pipelineId/stop?stopSubscription=true" -H 'accept: */*' -d ''
      echo "✅ Stopped pipeline"
      sleep 2  # optional: give some time for shutdown
    fi

    echo "🗑️  Deleting pipeline: $pipelineId"
    # Uncomment to actually delete:
    curl -s -X DELETE "$DELETE_URL_BASE/$pipelineId?deleteSubscription=true" -H 'accept: */*'
    echo "✅ Deleted: $pipelineId"
    echo "-----------------------------"
  fi
done

# Cleanup
rm -f "$TMP_FILE"
