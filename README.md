# Kanban2 App (Angular + Spring Boot 4, Java 25, Keycloak, PostgreSQL)

## Aperçu
- Frontend Angular 17 (SPA Kanban)
- Backend multi-modules Maven (Spring Boot 4.0.0, Java 25):
  - `bff-service` (OIDC, API façade)
  - `task-service`, `board-service`, `user-service` (DDD, JPA, Flyway, Actuator, Resource Server)
- Keycloak (realm `kanban-realm`)
- PostgreSQL
- Conteneurisation complète via Docker Compose

## Prérequis

Outils:
- Java 25, Maven 3.9+
- Node 20+, npm 10+
- Docker / Docker Compose

Configuration:
- ajouter dans /etc/hosts 
```bash
127.0.0.1   localhost-keycloak
```

## Lancer 

Avec Docker Compose:
```bash
cp env.template .env
docker compose up --build
```
Avec Make:
```bash
make all
```
Points d’entrée externes:
- Frontend Angular (via nginx): http://localhost:8080
- Keycloak: https://localhost-keycloak:8085 (admin: admin/admin, cert auto-signé)
- PostgreSQL: localhost:5432
- Monitoring: Prometheus http://localhost:9090, Grafana http://localhost:3000 (admin/admin)
Le BFF et les microservices ne sont plus exposés directement: le front appelle uniquement le BFF via `http://localhost:8080/api`.

## Endpoints principaux
| Composant | Base URL | Endpoint | Notes |
| --- | --- | --- | --- |
| Frontend | `http://localhost:8080` | `/` | SPA servie par nginx |
| BFF (via frontend) | `http://localhost:8080/api` | `/boards/**`, `/tasks/**`, `/users/**` | Proxy vers microservices |
| Keycloak | `https://localhost-keycloak:8085` | `/realms/kanban-realm/.well-known/openid-configuration` | Cert auto-signé; ajouter `127.0.0.1 localhost-keycloak` dans `/etc/hosts` |
| Grafana | `http://localhost:3000` | `/` | admin/admin, datasource Prometheus provisionnée |
| Prometheus | `http://localhost:9090` | `/` | Scrape des services sur /actuator/prometheus |

## Supervision (Prometheus / Grafana)
- Export Prometheus activé sur tous les services Spring Boot via `/actuator/prometheus`.
- Stack monitoring intégrée au `docker-compose.yml` :
  - Prometheus: http://localhost:9090 (scrape des microservices).
  - Grafana: http://localhost:3000 (admin/admin), datasource Prometheus provisionnée.
  - Dashboard provisionné: “Kanban Platform - Overview” (CPU process/system, heap, threads, HikariCP, RPS/latence HTTP, erreurs, GC, uptime) avec variable `service`.
- Pour lancer avec la supervision: `docker compose up` (après build des backends pour embarquer le registry Prometheus).

## OIDC (BFF)
- Flow: Authorization Code; PKCE imposé pour le client public, client confidentiel pour le BFF
- Clients:
  - `kanban-frontend` (public, PKCE) pour l’UI
  - `kanban-bff` (confidential) pour le back-channel et l’échange de token
- BFF: endpoints back-channel en HTTP interne vers Keycloak (`keycloak:8080`), redirections navigateur vers `https://localhost-keycloak:8085`; callback OAuth: `http://localhost:8080/login/oauth2/code/keycloak`.
- Sessions: Keycloak gère ses cookies sur `localhost-keycloak`; le BFF gère un cookie `BFFSESSIONID` sur `localhost` (config overridable via `BFF_SESSION_*`).
- L’utilisateur doit accepter le certificat auto-signé de `https://localhost-keycloak:8085` lors du premier login.
- Accès Keycloak en HTTPS: un conteneur dédié (`keycloak-certgen`) génère un certificat auto-signé et un keystore partagés via volume; les conteneurs backend importent ce certificat dans leurs truststores avant de démarrer.

## Bases de données
- PostgreSQL unique avec 3 schémas: `board_schema`, `task_schema`, `user_schema`.
- Flyway par microservice pour créer les tables.

## Diagramme (Mermaid)
```mermaid
graph LR
  %% ===== FRONTEND =====
  AngularFrontend[Angular Frontend]
  
  %% ===== BFF =====
  BFF[BFF API Gateway]

  %% ===== MICROSERVICES =====
  BoardService[Board Service]
  TaskService[Task Service]
  UserService[User Service]

  %% ===== DATABASE =====
  PostgreSQL[(PostgreSQL)]

  %% ===== IDENTITY PROVIDER =====
  subgraph IDP["Identity Provider (OIDC)"]
    Keycloak[Keycloak]
  end

  %% ===== FLOWS =====
  AngularFrontend -->|HTTP| BFF
  AngularFrontend -->|HTTPS login user/password| Keycloak

  BFF -->|REST/JSON + Bearer Token| BoardService
  BFF -->|REST/JSON + Bearer Token| TaskService
  BFF -->|REST/JSON + Bearer Token| UserService

  BFF -->|OIDC Auth Code Flow + PKCE Back-channel| Keycloak

  BoardService -->|JWKS validation| Keycloak
  TaskService -->|JWKS validation| Keycloak
  UserService -->|JWKS validation| Keycloak

  BoardService -->|JDBC| PostgreSQL
  TaskService -->|JDBC| PostgreSQL
  UserService -->|JDBC| PostgreSQL

  %% ===== STYLES =====
  %% Frontend
  style AngularFrontend fill:#e3f2fd,stroke:#1565c0,stroke-width:2px

  %% BFF
  style BFF fill:#fff3e0,stroke:#ef6c00,stroke-width:2px

  %% Microservices
  style BoardService fill:#f3e5f5,stroke:#6a1b9a
  style TaskService fill:#f3e5f5,stroke:#6a1b9a
  style UserService fill:#f3e5f5,stroke:#6a1b9a

  %% Database
  style PostgreSQL fill:#eceff1,stroke:#37474f,stroke-width:2px

  %% Keycloak
  style Keycloak fill:#b6e3b6,stroke:#2e7d32,stroke-width:3px

  %% ===== SECURITY FLOWS (GREEN) =====
  linkStyle 1 stroke:#2e7d32,stroke-width:2px
  linkStyle 5 stroke:#2e7d32,stroke-width:2px
  linkStyle 6 stroke:#2e7d32,stroke-width:2px
  linkStyle 7 stroke:#2e7d32,stroke-width:2px
  linkStyle 8 stroke:#2e7d32,stroke-width:2px
  ```

## Tests
- Unitaires: JUnit 5, AssertJ, Mockito
- Intégration: Spring Boot Test + Testcontainers PostgreSQL

## Dév rapide
```bash
# BFF
mvn -pl bff-service spring-boot:run
# Board service
mvn -pl board-service spring-boot:run
# Task service
mvn -pl task-service spring-boot:run
# User service
mvn -pl user-service spring-boot:run
# Frontend
cd frontend && npm start
```
Configurer les variables d’environnement (voir `docker-compose.yml` et `application.yml` de chaque module).
