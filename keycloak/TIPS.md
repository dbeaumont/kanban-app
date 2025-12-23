# Pb accès au volume dans keycloak

Pourquoi le volume keycloak-certs que j'ai monté en rw dans keycloak génère un Permission denied lors de la création du fichier KEYSTORE_FILE dans le entrypoint.sh ?


Parce que le conteneur Keycloak tourne sous l’utilisateur non‑root (uid 1000) et le volume keycloak-certs monté en /opt/keycloak/certs appartient à root avec des droits 755. Du coup, l’uid 1000 n’a pas le droit d’écrire dans ce répertoire et keytool échoue sur keystore.p12 avec “Permission denied”, même si le volume est monté en rw. Solutions : donner les bons droits/uid au volume (chown 1000:1000 ou chmod 775), ou démarrer root le temps de générer le keystore, ou précréer le fichier avec des permissions adéquates.

# Vérification de la base

Tu peux interroger Postgres via le conteneur :
```bash
docker compose exec -T postgres psql -U kanban -d kanban -c "\dt *.*"
```
ou pour les tables d’un schéma précis (ex. board_schema) :
```bash
docker compose exec -T postgres psql -U kanban -d kanban -c "\dt board_schema.*"
````
Pour voir aussi la table d’historique Flyway :
```bash
docker compose exec -T postgres psql -U kanban -d kanban -c "\dt board_schema.flyway_schema_history"
```