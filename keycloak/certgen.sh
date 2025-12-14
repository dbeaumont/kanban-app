#!/usr/bin/env sh
set -eu

CERT_DIR=${CERT_DIR:-/certs}
KEYSTORE_FILE="$CERT_DIR/keystore.p12"
KEYSTORE_PASS=${KEYSTORE_PASS:-changeit}
ALIAS=${KEYSTORE_ALIAS:-localhost-keycloak}

mkdir -p "$CERT_DIR"

if [ -f "$KEYSTORE_FILE" ]; then
  echo "Keystore already present at $KEYSTORE_FILE, skipping generation."
  exit 0
fi

keytool -genkeypair \
  -alias "$ALIAS" \
  -keyalg RSA \
  -storetype PKCS12 \
  -keystore "$KEYSTORE_FILE" \
  -storepass "$KEYSTORE_PASS" \
  -keypass "$KEYSTORE_PASS" \
  -dname "CN=$ALIAS" \
  -validity 365

keytool -exportcert \
  -alias "$ALIAS" \
  -keystore "$KEYSTORE_FILE" \
  -storetype PKCS12 \
  -storepass "$KEYSTORE_PASS" \
  -rfc \
  -file "$CERT_DIR/${ALIAS}.crt"

# Keycloak runs as uid 1000 by default; set ownership accordingly.
#chown 1000:1000 "$CERT_DIR"/"$ALIAS".crt "$KEYSTORE_FILE"

echo "Keystore and certificate generated in $CERT_DIR"
