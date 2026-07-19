FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine-3.23
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder --chown=appuser:appgroup /build/target/service-desk-api.jar app.jar

USER appuser
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]