#!/bin/bash

set -e  # Exit immediately if a command exits with a non-zero status

# --- CONFIGURATION ---
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"  # Get the script's directory

# --- FUNCTIONS ---
run_script() {
  local script_name="$1"
  echo "[$(date '+%Y-%m-%d %H:%M:%S')] ▶️  Running: $script_name"
  if "$SCRIPT_DIR/$script_name"; then
    echo "✅ Completed: $script_name"
  else
    echo "❌ Failed: $script_name"
    exit 1
  fi
  echo "-----------------------------"
}

# --- MAIN EXECUTION ---
run_script 1-removespacedeckPipelines.sh
run_script 2-removeSpacedeckServices.sh
run_script 3-removeSpaceMirrorWBservice.sh
run_script 4-removeTablespostgres.sh
run_script 5-recreateKafkaTopic.sh
run_script 6-deploySpaceMirrorWBservice.sh

echo "🎉 All scripts executed successfully!"
