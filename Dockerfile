FROM eclipse-temurin:17-jre-alpine-3.23
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --chown=appuser:appgroup target/service-desk-api.jar app.jar

USER appuser
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]