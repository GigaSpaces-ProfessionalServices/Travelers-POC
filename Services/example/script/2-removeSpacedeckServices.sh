#!/bin/bash
# --- Config ---
DEPLOYMENT_NAME="service-creator"
NAMESPACE="dih"
BASE_API_URL="https://travelers.labs.gigaspaces.net/api/service"

# Get one pod name
POD=$(kubectl get pods -n "$NAMESPACE" --selector="app.kubernetes.io/name=$DEPLOYMENT_NAME" -o jsonpath='{.items[0].metadata.name}')

if [ -z "$POD" ]; then
  echo "❌ No pod found for deployment: $DEPLOYMENT_NAME"
  exit 1
fi

echo "✅ Using pod: $POD"

# Get raw service list
echo "📥 Fetching services..."
kubectl exec -n "$NAMESPACE" "$POD" -- curl -s http://localhost:8080/services > services_raw.json

# Filter services with spaceName == "space" and status.status == "DEPLOYED"
jq -c '.[] | select(.spaceName == "space" and .status.status == "DEPLOYED")' services_raw.json > filtered_services.json

if [ ! -s filtered_services.json ]; then
  echo "ℹ️ No services to undeploy."
  exit 0
fi

# Loop over matching services
while read -r service; do
  SERVICE_NAME=$(echo "$service" | jq -r '.serviceName')
  echo "🔁 Processing service: $SERVICE_NAME"
  kubectl exec -n "$NAMESPACE" "$POD" -- curl -X DELETE -s http://localhost:8080/services/$SERVICE_NAME
#  curl -s -X DELETE "$BASE_API_URL/delete-service/$SERVICE_NAME"
  echo "✅ Done with: $SERVICE_NAME"
  echo "-----------------------------"
done < filtered_services.json

# Cleanup
rm -f services_raw.json filtered_services.json
