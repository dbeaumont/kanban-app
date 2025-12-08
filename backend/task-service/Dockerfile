# Build stage
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml pom.xml
COPY task-service/pom.xml task-service/pom.xml
COPY task-service/src task-service/src
RUN mvn -q -e -DskipTests -f task-service/pom.xml package

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
#RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/task-service/target/task-service-1.0.0.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
