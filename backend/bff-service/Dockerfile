# Build stage
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml pom.xml
COPY bff-service/pom.xml bff-service/pom.xml
COPY bff-service/src bff-service/src
#RUN mvn -q -e -DskipTests -f bff-service/pom.xml package
RUN mvn -e -DskipTests -f bff-service/pom.xml package

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
#RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/bff-service/target/bff-service-1.0.0.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
