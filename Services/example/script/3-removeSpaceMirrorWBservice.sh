#!/bin/bash
DEPLOYMENT_FILE="/home/jay/work/gigaspace/Travelers/Travelers-POC/Services/example/consumer-postgres/deployment.yaml"
NAMESPACE="dih"

echo "🗑️ Deleting deployment via kubectl from $DEPLOYMENT_FILE..."
kubectl delete -f "$DEPLOYMENT_FILE"
echo "✅ Deployment deleted."

echo "🚀 Uninstalling Helm release: mirror in namespace $NAMESPACE..."
helm uninstall mirror -n "$NAMESPACE"
echo "✅ Helm release 'mirror' uninstalled."

echo "🚀 Uninstalling Helm release: space in namespace $NAMESPACE..."
helm uninstall space -n "$NAMESPACE"
echo "✅ Helm release 'space' uninstalled."
