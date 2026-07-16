FROM maven:3.9-eclipse-temurin-17
WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src ./src

RUN ./mvnw clean package -DskipTests


ENTRYPOINT ["java", "-jar", "target/api-0.0.1-SNAPSHOT.jar"]