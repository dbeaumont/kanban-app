# Analyse du projet Kanban

## 1. Aperçu de l’architecture du projet

Le dépôt décompressé `kanban2-app` contient une plateforme **Kanban** complète :

- **Front‑end Angular 17** : SPA servie via Nginx (`frontend/`). Un module `environment.ts` définit l’URL de base de l’API (`/api`), qui est utilisée pour toutes les requêtes HTTP. Le bouton *Login* redirige vers `/oauth2/authorization/keycloak` exposé par le **BFF**.
- **Back‑end multi‑modules Spring Boot 4 (Java 25)** : un projet Maven parent (`backend/pom.xml`) avec quatre sous‑modules :
  - `bff-service` : joue le rôle de **Backend For Frontend**, gère le flux OIDC, expose les endpoints `/api/boards`, `/api/tasks`, `/api/users` et sert de proxy vers les microservices internes. La configuration de sécurité définit deux chaînes de filtres : une pour l’API (`/api/**`, authentifiée via cookies) et une pour les pages web (gestion de la redirection OIDC).
  - `board-service`, `task-service`, `user-service` : microservices gérant chacun un domaine. Chaque service suit un découpage Domain‑Driven Design minimal : `domain/` (entités JPA), `application/` (services métier), `infrastructure/` (repositories JPA), `api/` (contrôleurs REST).
- **Keycloak** (répertoire `keycloak/`) : démarre en mode dev via un script d’entrée personnalisé. Un certificat auto‑signé est généré pour exposer Keycloak en HTTPS sur `localhost-keycloak:8085`. L’export de realm (`realm-export.json`) préconfigure les clients OIDC `kanban-frontend` (public) et `kanban-bff` (confidential).
- **Base de données PostgreSQL** : un seul conteneur géré par Compose, avec trois schémas (`board_schema`, `task_schema`, `user_schema`) créés par Flyway.
- **Supervision : Prometheus & Grafana** via le dossier `monitoring/` et configurées dans `docker-compose.yml`.

Cette architecture suit une approche **microservices** avec un BFF central pour l’authentification et le routage. Les services Java respectent une séparation en couches (domaine, application, infrastructure), proche de la Clean Architecture【863047441109094†L78-L120】. Le README décrit les flux OIDC (code + PKCE) et le rôle de chaque composant.

## 2. Observations sur le code et bonnes pratiques

### 2.1 Structure et séparation en couches

- Les microservices `board-service`, `task-service` et `user-service` suivent un découpage clair : `domain` (entités), `application` (services métier), `infrastructure` (repositories), `api` (contrôleurs REST). Cette structure respecte les principes de la Clean Architecture : la logique métier est séparée des adaptateurs et de l’exposition HTTP【863047441109094†L78-L120】. 
- Les entités JPA utilisent des identifiants de type `String` générés via `UUID`. Pour simplifier la persistance et éviter les conversions, l’utilisation d’identifiants numériques (`Long` avec `@GeneratedValue`) pourrait être envisagée. 
- Les DTO exposés par le BFF utilisent des `record` Java, ce qui réduit le code boilerplate. C’est une bonne pratique pour des objets immuables transférés au front. 
- Chaque module possède un `SecurityConfig` qui configure Spring Security : ressources API protégées, CSRF désactivé (pertinent pour API JSON), gestion des sessions stateless et intégration du support JWT (microservices) ou OIDC client + login (BFF). 
- Le BFF différencie la sécurité des endpoints `/api/**` (protégés par la session et CSRF) et des endpoints publics (`/oauth2/**`, `/login`, `/logout`). Cela permet de gérer la redirection automatique des utilisateurs vers Keycloak en cas de 401.

### 2.2 Points d’amélioration du code

- **Implémentation de `equals`/`hashCode`** : les entités JPA (`Board`, `Task`, `User`) ne redéfinissent pas `equals` et `hashCode`. Cela peut poser problème lors de l’utilisation en collections ou dans Hibernate ; il est recommandé de les implémenter de manière cohérente (basée sur l’identifiant).
- **Validations sur les entrées API** : certaines requêtes des services (`BoardController`, `TaskController`) ne valident pas les champs d’entrée (par exemple, l’absence de `@Valid` sur certaines méthodes). Il serait préférable d’utiliser des DTO spécifiques annotés (`@NotBlank`, `@Size`, etc.) pour la création et la mise à jour, afin de ne pas exposer directement les entités JPA et d’assurer une validation automatique.
- **Gestion des exceptions** : en cas de ressource non trouvée, `repository.findById(id).orElseThrow()` lève une exception générique. Mettre en place un gestionnaire d’exceptions global (`@ControllerAdvice`) permet de retourner des erreurs HTTP claires (404, 400, 500) au front.
- **Couche Application** : les services métiers `BoardService`, `TaskService`, `UserService` ne contiennent pour l’instant que de la simple manipulation de données. S’ils évoluent, il peut être utile de définir des interfaces (ports) et d’injecter les implémentations de la couche infrastructure via un pattern hexagonal, ce qui facilitera les tests et le remplacement éventuel des adaptateurs.
- **Mapping entité/DTO** : le BFF convertit les objets des microservices en `record` manuellement. L’intégration d’un mapper (MapStruct) permettrait d’automatiser la conversion, d’ajouter ou masquer certains champs et d’éviter des erreurs lors de l’évolution du modèle.

### 2.3 Frontend Angular

- Le front utilise la **standalone API** d’Angular 17, ce qui supprime la surcouche de modules. La configuration `environment.ts` se limite à `apiBaseUrl`; on pourrait y ajouter des paramètres pour l’URL du BFF ou du serveur Keycloak si l’application devait s’exécuter sous différents domaines.
- Le front redirige vers `/oauth2/authorization/keycloak` en cas de 401 via un intercepteur HTTP. Cette redirection est codée en dur ; elle pourrait être calculée à partir d’une base d’URL stockée dans l’environnement pour plus de flexibilité.
- La gestion des erreurs 403 (forbidden) n’est pas couverte ; ajouter une gestion dédiée permettrait d’afficher une page ou un message spécifique.
- Pour améliorer l’expérience utilisateur, on peut envisager un stockage local (IndexedDB ou localStorage) du profil utilisateur (nom, e‑mail) récupéré via `/userinfo` par le BFF.

## 3. Rationalisation des variables d’environnement OIDC et Keycloak

### 3.1 Variables actuelles

Le `docker-compose.yml` définit plusieurs variables pour la BFF :

- `OIDC_AUTH_URI`, `OIDC_TOKEN_URI`, `OIDC_USERINFO_URI`, `OIDC_JWKS_URI`
- `OIDC_CLIENT_ID`, `OIDC_CLIENT_SECRET`, `OIDC_REDIRECT_URI`
- `FRONTEND_ORIGIN`, `BOARD_SERVICE_URL`, `TASK_SERVICE_URL`, `USER_SERVICE_URL`

Les microservices définissent :`OIDC_ISSUER_URI` et `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI`. Ce dernier est facultatif car Spring Boot peut découvrir les clés via le *discovery endpoint*. 

### 3.2 Problèmes observés

1. **Duplication des URI** : chaque variable est définie manuellement avec l’URL complète (`/realms/kanban-realm/protocol/openid-connect/…`). Une faute de frappe ou un changement de domaine nécessite de mettre à jour plusieurs variables.
2. **Incohérence HTTP/HTTPS** : le BFF utilise `https://localhost-keycloak:8085` pour `OIDC_AUTH_URI` (redirections navigateur) et `http://keycloak:8080` pour `OIDC_TOKEN_URI`/`USERINFO_URI`/`JWKS_URI`. Cette distinction est nécessaire en développement, mais elle complique la configuration et n’est pas explicitée par les noms des variables.
3. **Nommage hétérogène** : `OIDC_ISSUER_URI` pour les microservices, `OIDC_TOKEN_URI`/`OIDC_AUTH_URI` pour le BFF. Il peut être difficile de comprendre rapidement quelles variables sont nécessaires à quel module.

### 3.3 Recommandations de rationalisation

1. **Définir des variables de base**

   Créer des variables d’environnement pour la **base externe** (utilisée par les navigateurs) et la **base interne** (utilisée dans le réseau Docker) ainsi que le **realm** :

   ```yaml
   KEYCLOAK_EXTERNAL_URL=https://localhost-keycloak:8085
   KEYCLOAK_INTERNAL_URL=http://keycloak:8080
   KEYCLOAK_REALM=kanban-realm
   ```

   À partir de ces bases, les scripts ou la configuration Spring peuvent construire dynamiquement les endpoints OIDC :

   ```yaml
   OIDC_AUTH_URI=${KEYCLOAK_EXTERNAL_URL}/realms/${KEYCLOAK_REALM}/protocol/openid-connect/auth
   OIDC_TOKEN_URI=${KEYCLOAK_INTERNAL_URL}/realms/${KEYCLOAK_REALM}/protocol/openid-connect/token
   OIDC_USERINFO_URI=${KEYCLOAK_INTERNAL_URL}/realms/${KEYCLOAK_REALM}/protocol/openid-connect/userinfo
   OIDC_JWKS_URI=${KEYCLOAK_INTERNAL_URL}/realms/${KEYCLOAK_REALM}/protocol/openid-connect/certs
   OIDC_ISSUER_URI=${KEYCLOAK_EXTERNAL_URL}/realms/${KEYCLOAK_REALM}
   ```

   Cette approche centralise la définition de l’hôte et du realm et limite les risques d’incohérence. En production, `KEYCLOAK_EXTERNAL_URL` et `KEYCLOAK_INTERNAL_URL` pourront pointer vers le même domaine (pas de mélange HTTP/HTTPS).

2. **Normaliser le nom des variables**

   Utiliser une convention commune pour tous les services : `OIDC_ISSUER_URI`, `OIDC_JWKS_URI`, `OIDC_CLIENT_ID`, `OIDC_CLIENT_SECRET`, `OIDC_REDIRECT_URI`. Le BFF peut dériver `authorization-uri` et `token-uri` à partir de `issuer-uri` via la découverte OIDC. Spring Boot permet de n’indiquer que `issuer-uri`, et il déduira automatiquement les autres endpoints. Cela simplifie la configuration et évite d’exposer par erreur des endpoints internes.

3. **Externaliser les secrets**

   Le secret du client (`OIDC_CLIENT_SECRET`) ne doit pas être stocké en clair dans `docker-compose.yml`. Utiliser un gestionnaire de secrets (Vault, Docker secrets, variables d’environnement injectées par CI/CD). Le tutoriel sur la configuration d’un client OIDC recommande de stocker `KEYCLOAK_CLIENT_SECRET` dans un fichier `.env` non versionné et d’utiliser une fonction `requireEnv()` pour vérifier sa présence【621464624591494†L237-L244】. 

4. **Partager la configuration entre services**

   Plutôt que de dupliquer les mêmes variables pour chaque microservice, créer un fichier `.env` ou un `docker-compose.override.yml` avec les paramètres communs (URL base, realm, DB). Utiliser les ancres YAML ou un service de configuration centralisé (Spring Cloud Config ou Consul) pour propager la configuration à l’ensemble des services.

5. **Utiliser les options Keycloak standard**

   Si vous déployez Keycloak en production, configurez la base de données via les variables `KC_DB`, `KC_DB_URL`, `KC_DB_USERNAME`, `KC_DB_PASSWORD` recommandées par la documentation Keycloak【174455341104614†L94-L110】. Pour activer HTTPS, utilisez `KC_HOSTNAME`, `KC_HTTPS_CERTIFICATE_FILE` et `KC_HTTPS_CERTIFICATE_KEY_FILE`【174455341104614†L190-L200】.

## 4. Améliorations suggérées pour la sécurité et l’exploitation

- **Mettre à jour Keycloak** : la version utilisée (`23.0.7`) n’est pas la plus récente. Les nouvelles versions corrigent des vulnérabilités et ajoutent des fonctionnalités. Suivre les mises à jour régulières comme recommandé par Inero Software【995902357029837†L159-L174】.
- **Sécuriser l’administration Keycloak** : exposer la console d’administration sur un sous‑domaine distinct (ex : `keycloak-admin.example.com`) et la protéger via un réseau interne ou un VPN. La documentation recommande de ne pas exposer les API d’administration sur le même domaine que les endpoints publics【603609833642394†L51-L59】.
- **Configurer les durées de session** : ajuster `accessTokenLifespan` et `refreshTokenLifespan` dans Keycloak selon le niveau de sécurité attendu (sessions courtes pour des données sensibles). En parallèle, configurer le cookie `BFFSESSIONID` pour qu’il soit `Secure` et `SameSite=None` lors du passage en production.
- **Activer la rotation des secrets de client** : Keycloak permet de régénérer le secret d’un client; automatiser ce processus augmente la sécurité. Prévoir la rotation via un job planifié et recharger la configuration du BFF sans interruption.

## 5. Conclusion

Le projet Kanban montre une architecture bien pensée : séparation claire entre front‑end et back‑end, microservices Spring Boot respectant la Clean Architecture et intégration d’un BFF pour centraliser l’authentification. Les principales améliorations concernent :

1. **Code** : implémenter `equals`/`hashCode` pour les entités, valider les entrées via des DTO dédiés, gérer les exceptions globalement et automatiser le mapping entité/DTO.
2. **Architecture** : renforcer l’isolation du domaine via des interfaces et injection de dépendances, enrichir les tests (unitaires et intégration), et améliorer la gestion des erreurs côté front.
3. **OIDC/Keycloak** : rationaliser les variables d’environnement en définissant des bases d’URL et un realm, normaliser le nom des variables, externaliser les secrets et utiliser les options standard de Keycloak pour la base de données et le TLS【174455341104614†L94-L110】. 

En appliquant ces ajustements, vous améliorerez la maintenabilité, la sécurité et la portabilité de votre plateforme Kanban.
