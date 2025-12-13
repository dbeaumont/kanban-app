#!/bin/sh
set -e

CERT_DIR=${CERT_DIR:-/opt/keycloak/certs}
KEYSTORE_FILE="$CERT_DIR/keystore.p12"
KEYSTORE_PASS=${KEYSTORE_PASS:-changeit}

mkdir -p "$CERT_DIR"

# Generate a self-signed cert with keytool (avoids needing openssl in the image)
if [ ! -f "$KEYSTORE_FILE" ]; then
  keytool -genkeypair \
    -alias localhost-keycloak \
    -keyalg RSA \
    -storetype PKCS12 \
    -keystore "$KEYSTORE_FILE" \
    -storepass "$KEYSTORE_PASS" \
    -keypass "$KEYSTORE_PASS" \
    -dname "CN=localhost-keycloak" \
    -validity 365
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
