FROM maven:3.9.16-eclipse-temurin-21-alpine AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn --batch-mode dependency:go-offline

COPY src src
RUN mvn --batch-mode package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S atlas && adduser -S atlas -G atlas
COPY --from=build --chown=atlas:atlas /workspace/target/atlas-*.jar app.jar

USER atlas
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

