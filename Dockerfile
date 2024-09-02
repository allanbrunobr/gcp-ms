FROM openjdk:17-jdk-slim AS build

RUN apt-get update && \
    apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-8.8-bin.zip -P /tmp && \
    unzip /tmp/gradle-8.8-bin.zip -d /opt && \
    ln -s /opt/gradle-8.8/bin/gradle /usr/bin/gradle

WORKDIR /app

COPY . .

RUN gradle build -x test --no-daemon

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs /app/libs

EXPOSE 8081

ENV GOOGLE_APPLICATION_CREDENTIALS=/app/gcp-credentials.json
ENV SPRING_CLOUD_GCP_PROJECT_ID=app-springboot-project

ENTRYPOINT ["java", "-jar", "/app/libs/gcp-ms.jar"]
