#!/bin/sh
set -e

echo "Waiting for Vault to be ready..."
for i in 1 2 3 4 5 6 7 8 9 10; do
  if vault status >/dev/null 2>&1; then
    break
  fi
  echo "Waiting... ($i/10)"
  sleep 2
done

echo "Initializing secrets in Vault..."
vault kv put secret/kokoro/key-v1 key="${ENCRYPTION_KEY}"

echo "Vault secrets initialized successfully!"
