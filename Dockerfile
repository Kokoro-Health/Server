FROM gradle:8-jdk21-alpine AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .

RUN gradle :api:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S kokoro && adduser -S kokoro -G kokoro
USER kokoro

WORKDIR /app

COPY --from=build /home/gradle/src/api/build/libs/api-1.0.0-SNAPSHOT.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:+UseParallelGC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
