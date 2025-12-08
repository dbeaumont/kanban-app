# Build stage
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml pom.xml
COPY user-service/pom.xml user-service/pom.xml
COPY user-service/src user-service/src
RUN mvn -q -e -DskipTests -f user-service/pom.xml package

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app
#RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
COPY --from=build /app/user-service/target/user-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
