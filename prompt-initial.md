# Prompt de demande de prompt

Crée un prompt pour générer un projet de gestion de tâches de type kanban. Je veux une application front angular et un backend en springboot. Le backend sera composé de plusieurs microservices pour porter les traitements métier. Il y aura aussi un module BFF qui portera toutes les interactions avec les services métiers. Ce BFF portera aussi toute la gestion OpenIDConnect en mode authorization code flow. L'IdP OIDC sera Keycloak. Il faudra gérer le CORS entre le front angular, le Keycloak et le BFF. Les microservices métier s'appuieront sur une base de données PostgreSQL. Tous les modules seront déployés dans un container docker dédié et le tout sera orchestré avec docker compose. Spring Security sera utilisé par le BFF pour porter OIDC. Maven sera utilisé pour les modules Java. Java sera en version 25. SpringBoot sera en version 4.0.0. Les microservices suivront une architecture DDD. Crée une application de niveau entreprise, complète et clairement structurée, avec des commentaires dans le code, un README.md qui explique le projet, qui récapitule les endpoints et qui présente un diagramme d'architecture général en mermaid. Le code java sera outillé avec des tests unitaires et des tests d'intégration. 

# Prompt généré

Tu es un assistant expert en architecture logicielle d’entreprise, en Java/Spring Boot, Angular, Keycloak, Docker et Docker Compose.  
Ta mission est de générer **un projet complet de gestion de tâches de type Kanban**, prêt à être cloné et lancé, avec une architecture microservices.

---

### 1. Objectif général

Générer **un projet d’application Kanban de niveau entreprise**, comprenant :

- Un **frontend Angular** (application SPA Kanban).
- Un **backend Java** structuré en plusieurs modules Maven :
  - Un **module BFF (Backend For Frontend)**, exposé en HTTP/HTTPS au frontend.
  - Plusieurs **microservices métier** (architecture **DDD**).
- Un **Keycloak** comme **IdP OpenID Connect** (OIDC).
- Une base de données **PostgreSQL**.
- Tous les modules Java packagés en **containers Docker**, orchestrés avec **Docker Compose**.

---

### 2. Contraintes techniques

1. **Langages et versions :**
   - Java : **version 25**
   - Framework backend : **Spring Boot 4.0.0**
   - Build backend : **Maven**
   - Frontend : **Angular** (version récente stable, précise-la dans le projet)

2. **Architecture backend :**
   - Projet parent Maven **multi-modules**.
   - Modules :
     - `bff-service` (BFF)
     - `task-service` (microservice métier pour la gestion des tâches)
     - `board-service` (microservice pour la gestion des tableaux/colonnes)
     - `user-service` (microservice pour la gestion utilisateur interne à l’app, distinct de Keycloak)
   - Chaque microservice applique une **architecture DDD** :
     - `domain` (agrégats, entités, value objects, repository interfaces, services de domaine)
     - `application` (use cases / services applicatifs, DTO, mapping)
     - `infrastructure` (adapters, repositories JPA, configuration, clients REST vers autres services)

3. **Sécurité & OIDC :**
   - **Keycloak** comme IdP OIDC.
   - Le **BFF** gère tout le **OpenID Connect** avec **authorization code flow** (sans PKCE).
   - Utilisation de **Spring Security** dans le BFF pour gérer :
     - Authentification OIDC.
     - Extraction du token d’accès.
     - Propagation des tokens vers les microservices métier.
   - Configuration de **CORS** :
     - Entre le frontend Angular et le BFF.
     - Entre le frontend et Keycloak (si nécessaire pour le login).
     - Les origines autorisées doivent être claires et configurables (variables d’environnement).

4. **Persistance :**
   - Les microservices métier utilisent **PostgreSQL**.
   - Un schéma par microservice ou un schéma partagé clairement documenté.
   - Utilisation de **Spring Data JPA** et migration de schéma via **Flyway** ou **Liquibase** (choisir l’un et le documenter).

5. **Tests :**
   - **Tests unitaires** (JUnit 5, AssertJ, Mockito, etc.).
   - **Tests d’intégration** :
     - Démarrage du contexte Spring.
     - Utilisation de **Testcontainers** pour PostgreSQL (et, si possible, Keycloak en test).

6. **Conteneurisation & orchestration :**
   - Un **Dockerfile** par module Java (BFF + chaque microservice).
   - Un **Dockerfile** pour le frontend Angular (build + nginx ou équivalent pour servir la SPA).
   - Un **docker-compose.yml** à la racine qui orchestre :
     - Frontend Angular
     - BFF
     - Microservices métier
     - PostgreSQL
     - Keycloak
   - Configuration de **réseaux Docker** pour permettre la communication entre les containers.
   - Utilisation de variables d’environnement pour :
     - URLs de Keycloak
     - Clients OIDC
     - Identifiants DB
     - Ports
   - Prévoir une configuration minimaliste mais réaliste de **Keycloak** :
     - Un realm `kanban-realm`
     - Un client `kanban-frontend` (public ou conf adapté au code flow)
     - Un client `kanban-bff` si nécessaire
     - Quelques rôles/clients scopes typiques (ex. ROLE_USER, ROLE_ADMIN)

---

### 3. Fonctionnel Kanban attendu

Définir un modèle fonctionnel minimal mais cohérent, par exemple :

- **Board** (tableau) :
  - id, name, description, ownerId
- **Column** :
  - id, boardId, name, position
- **Task** :
  - id, boardId, columnId, title, description, status, assigneeId, labels, dueDate, createdAt, updatedAt
- **User** (dans user-service, en complément de Keycloak) :
  - id, keycloakUserId, displayName, email, préférences
- Opérations typiques exposées via BFF :
  - CRUD boards
  - CRUD columns d’un board
  - CRUD tasks (move task, change column, assign user, etc.)
  - Récupérer les informations utilisateur courantes à partir du token OIDC

---

### 4. Structure globale du projet

Tu dois présenter clairement la structure de fichiers, avec le format suivant pour la réponse :

- Pour chaque fichier : un bloc markdown

  ```text
  chemin/vers/le/fichier.ext
  ```

  suivi d’un bloc de code avec le contenu :

  ```langage
  // contenu du fichier
  ```

Par exemple :

```text
backend/pom.xml
```

```xml
<!-- contenu -->
```

```text
backend/bff-service/src/main/java/.../MainBffApplication.java
```

```java
// contenu
```

Idem pour le frontend Angular et les fichiers Docker.

---

### 5. Détails attendus pour le BFF

- Application Spring Boot 4.0.0 avec :
  - Auto-configuration de Spring Security / Spring OAuth2 Client.
  - Configuration OIDC (issuer-uri, client-id, client-secret si nécessaire, scopes).
  - Filtrage des requêtes :
    - Endpoints publics (ex: `/actuator/health`, `/login`, `/logout`).
    - Endpoints protégés (`/api/**`).
  - Contrôleurs REST exposant des endpoints orientés frontend :
    - `/api/boards/**`, `/api/tasks/**`, `/api/users/**`.
  - Clients REST (WebClient ou RestTemplate) pour appeler les microservices :
    - Ajouter le **Bearer token** OIDC dans les appels sortants.
- Gestion du CORS :
  - Configuration globale Spring Security (ou `CorsConfigurationSource`) pour autoriser l’origine du frontend Angular.

---

### 6. Détails attendus pour les microservices métier

Pour chaque microservice (`task-service`, `board-service`, `user-service`) :

- Structure DDD :
  - `domain` : entités, value objects, services de domaine, interfaces de repository.
  - `application` : services applicatifs, DTO, mapping.
  - `infrastructure` : implémentations JPA des repositories, config Spring, contrôleurs REST, mapping DTO <-> entités.
- Exposition d’API REST claires (exemple pour `task-service`) :
  - `GET /tasks`
  - `GET /tasks/{id}`
  - `POST /tasks`
  - `PUT /tasks/{id}`
  - `DELETE /tasks/{id}`
  - `PATCH /tasks/{id}/move` (changement de colonne/board)
- Sécurité :
  - Validation du token (soit via resource server Spring Security, soit en s’appuyant sur le BFF pour les appels).
- Accès PostgreSQL :
  - `application.yml` avec configuration datasource.
  - Scripts Flyway/Liquibase pour création de table.
- Ajout des métriques :
  - Utilisation de Spring Actuator

---

### 7. Frontend Angular

- Générer une application Angular typée "Kanban" avec :
  - Pages principales :
    - Page de login/redirection (en coordination avec Keycloak/BFF).
    - Page de liste des boards.
    - Page de détail d’un board avec colonnes et tâches affichées en mode Kanban (drag & drop si possible ou au moins boutons de déplacement).
  - Services Angular consommant le BFF :
    - `BoardApiService`, `TaskApiService`, `UserApiService`.
  - Gestion du token :
    - Soit interaction via redirections BFF, soit utilisation d’un client OIDC JS, mais le BFF doit rester le point central.
- Fichier de configuration (environnements Angular) pour pointer vers les URLs du BFF.
- Gestion de la souris pour déplacer les tickets du kanban

---

### 8. README.md à la racine

Tu dois générer un **README.md complet** qui :

1. Décrit l’architecture générale du projet.
2. Explique comment :
   - Construire le projet (Maven, npm).
   - Lancer le projet avec **Docker Compose** (`docker-compose up`).
   - Accéder à :
     - L’UI Angular.
     - Les endpoints BFF.
     - Les actuators si présents.
     - La console d’admin Keycloak.
3. Fournit la liste des principaux **endpoints REST** (BFF et microservices) sous forme de tableau.
4. Inclut un **diagramme d’architecture général en Mermaid**, du type :

   ```mermaid
   graph LR
     AngularFrontend -->|HTTPS| BFF
     BFF -->|REST/JSON, Bearer token| TaskService
     BFF -->|REST/JSON, Bearer token| BoardService
     BFF -->|REST/JSON, Bearer token| UserService
     TaskService -->|JDBC| PostgreSQL
     BoardService -->|JDBC| PostgreSQL
     UserService -->|JDBC| PostgreSQL
     BFF -->|OIDC (code flow)| Keycloak
     AngularFrontend -->|redirections login/logout| Keycloak
   ```

   (Tu peux l’ajuster pour refléter précisément l’architecture générée.)

---

### 9. Qualité du code

- Ajouter des **commentaires clairs** dans le code Java et TypeScript pour expliquer les classes principales, les responsabilités, les patterns DDD utilisés.
- Respecter de bonnes pratiques :
  - Package-by-feature / package-by-layer selon DDD.
  - Validation des entrées (Bean Validation).
  - Gestion d’erreurs REST (ExceptionHandlers globaux).
- Fournir quelques **tests unitaires** et **tests d’intégration** représentatifs (pas forcément exhaustifs, mais démonstratifs).

---

### 10. Format de la réponse

- La réponse doit contenir **l’ensemble du code du projet** (backend, frontend, Docker, README) dans des blocs de code clairement identifiés par chemin de fichier.
- Assure-toi que :
  - Le projet compile (du mieux possible dans le cadre de cette génération).
  - Les configurations sont cohérentes (ports, URLs, variables d’environnement par défaut).

---

Génère maintenant le projet complet directement sur le filesystem conformément à ces spécifications.
