# Build stage
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml pom.xml
COPY board-service/pom.xml board-service/pom.xml
COPY board-service/src board-service/src
#RUN mvn -q -e -DskipTests -f board-service/pom.xml package
RUN mvn -e -DskipTests -f board-service/pom.xml package

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
#RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/board-service/target/board-service-1.0.0.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
