#!/usr/bin/env sh
set -eu
set -x

CERT_FILE=${KEYCLOAK_CERT_FILE:-/opt/keycloak-certs/localhost-keycloak.crt}
TRUSTSTORE_PATH=${JAVA_TRUSTSTORE_PATH:-/app/keycloak-truststore.p12}
TRUSTSTORE_PASSWORD=${JAVA_TRUSTSTORE_PASSWORD:-changeit}

# If the Keycloak cert is present, import it into a local truststore
if [ -f "$CERT_FILE" ]; then
  if [ ! -f "$TRUSTSTORE_PATH" ]; then
    keytool -importcert -noprompt \
      -alias localhost-keycloak \
      -file "$CERT_FILE" \
      -keystore "$TRUSTSTORE_PATH" \
      -storetype PKCS12 \
      -storepass "$TRUSTSTORE_PASSWORD"
  fi
  JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Djavax.net.ssl.trustStore=$TRUSTSTORE_PATH -Djavax.net.ssl.trustStorePassword=$TRUSTSTORE_PASSWORD"
  export JAVA_TOOL_OPTIONS
fi

exec java ${JAVA_TOOL_OPTIONS:-} -jar app.jar
