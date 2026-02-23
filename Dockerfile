# Multi-stage Dockerfile: build with Gradle Wrapper on JDK 17, run with slim JRE
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /home/app
# Copiar todo el proyecto (incluye gradlew)
COPY . /home/app
# Asegurar que gradlew sea ejecutable y construir el jar
RUN chmod +x ./gradlew && ./gradlew clean bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# instalar curl para healthchecks
RUN apt-get update && apt-get install -y curl ca-certificates && rm -rf /var/lib/apt/lists/*
COPY --from=builder /home/app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
