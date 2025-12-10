# Build stage
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml pom.xml
COPY bff-service/pom.xml bff-service/pom.xml
# Récupération de toutes les dépendances avant de copier le code source et de compiler (utile pour la mise en cache docker sans dépendence au code source)
RUN mvn -B -f bff-service/pom.xml dependency:go-offline
# Récupération du code source et compilation
COPY bff-service/src bff-service/src
RUN mvn -e -DskipTests -f bff-service/pom.xml package

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/bff-service/target/bff-service-1.0.0.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
