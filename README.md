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
- Java 25, Maven 3.9+
- Node 20+, npm 10+
- Docker / Docker Compose

## Build
```bash
mvn clean package -DskipTests
cd frontend && npm install && npm run build
```

## Lancer avec Docker Compose
```bash
docker-compose up --build
```
Points d’entrée externes:
- Frontend Angular (via nginx): http://localhost:8080
- Keycloak: http://localhost-keycloak:8085 (admin: admin/admin)
- PostgreSQL: localhost:5432
- Monitoring: Prometheus http://localhost:9090, Grafana http://localhost:3000 (admin/admin)
Le BFF et les microservices ne sont plus exposés directement: le front appelle uniquement le BFF via `http://localhost:8080/api`.

## Endpoints principaux
| Composant | Base URL | Endpoint | Notes |
| --- | --- | --- | --- |
| Frontend | `http://localhost:8080` | `/` | SPA servie par nginx |
| BFF (via frontend) | `http://localhost:8080/api` | `/boards/**`, `/tasks/**`, `/users/**` | Proxy vers microservices |
| Keycloak | `http://localhost-keycloak:8085` | `/realms/kanban-realm/.well-known/openid-configuration` | OIDC discovery |
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
- Le BFF gère sa propre session (cookie distinct) et ajoute le bearer token aux appels microservices via WebClient.

## Bases de données
- PostgreSQL unique avec 3 schémas: `board_schema`, `task_schema`, `user_schema`.
- Flyway par microservice pour créer les tables.

## Diagramme (Mermaid)
```mermaid
graph LR
  AngularFrontend -->|HTTP| BFF
  AngularFrontend -->|HTTPS : login user/password | Keycloak
  BFF -->|REST/JSON, Bearer token| BoardService
  BFF -->|REST/JSON, Bearer token| TaskService
  BFF -->|REST/JSON, Bearer token| UserService
  BFF -->|Back-channel token exchange OIDC auth code flow + PKCE | Keycloak
  BoardService -->|JDBC| PostgreSQL
  TaskService -->|JDBC| PostgreSQL
  UserService -->|JDBC| PostgreSQL
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
