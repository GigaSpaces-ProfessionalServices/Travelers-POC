#!/bin/bash

# Namespace and pod selector
NAMESPACE="dih"
LABEL_SELECTOR="app=postgres"

# Find pod by label
POD=$(kubectl get pods -n "$NAMESPACE" -l "$LABEL_SELECTOR" -o jsonpath='{.items[0].metadata.name}')

# Fallback: fuzzy search
if [ -z "$POD" ]; then
  POD=$(kubectl get pods -n "$NAMESPACE" --no-headers | grep postgres | awk '{print $1}' | head -n 1)
fi

if [ -z "$POD" ]; then
  echo "❌ No Postgres pod found."
  exit 1
fi

echo "✅ Using pod: $POD"

USER="myuser"
DB="mydb"

# Get drop statements
DROP_STMTS=$(kubectl exec -n "$NAMESPACE" "$POD" -- bash -c "
psql -U $USER -d $DB -At -c \"
SELECT 'DROP TABLE IF EXISTS \\\"' || tablename || '\\\" CASCADE;'
FROM pg_tables WHERE schemaname='public';
\"")

# Run drop statements
echo "$DROP_STMTS" | while read -r stmt; do
  echo "🔸 Running: $stmt"
  kubectl exec -n "$NAMESPACE" "$POD" -- bash -c "psql -U $USER -d $DB -c \"$stmt\""
done

echo "✅ All tables dropped (if any existed)."
