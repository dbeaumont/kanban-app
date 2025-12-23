#!/usr/bin/env sh
set -eu
set -x

CERT_DIR=${CERT_DIR:-/opt/keycloak/certs}
KEYSTORE_FILE="$CERT_DIR/keystore.p12"
KEYSTORE_PASS=${KEYSTORE_PASS:-changeit}

mkdir -p "$CERT_DIR"
if [ ! -f "$KEYSTORE_FILE" ]; then
  echo "Keystore not found at $KEYSTORE_FILE. Ensure keycloak-certgen service ran successfully." >&2
  exit 1
fi

exec /opt/keycloak/bin/kc.sh start-dev \
  --import-realm \
  --health-enabled=true \
  --hostname-strict=false \
  --hostname-url=https://localhost-keycloak:8085 \
  --hostname-strict-https=false \
  --https-key-store-file="$KEYSTORE_FILE" \
  --https-key-store-type=PKCS12 \
  --https-key-store-password="$KEYSTORE_PASS" \
  --https-port=8085 \
  --http-enabled=true \
  --http-port=8080
