# Build stage
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml pom.xml
COPY board-service/pom.xml board-service/pom.xml
# Récupération de toutes les dépendances avant de copier le code source et de compiler (utile pour la mise en cache docker sans dépendence au code source)
RUN mvn -B -f board-service/pom.xml dependency:go-offline
# Récupération du code source et compilation
COPY board-service/src board-service/src
RUN mvn -e -DskipTests -f board-service/pom.xml package

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/board-service/target/board-service-1.0.0.jar app.jar
EXPOSE 8082
COPY board-service/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
ENTRYPOINT ["/app/entrypoint.sh"]
