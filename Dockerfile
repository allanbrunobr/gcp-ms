# Use uma imagem base do OpenJDK
FROM openjdk:17-jdk-slim AS build

# Instale dependências necessárias e Gradle
RUN apt-get update && \
    apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-8.8-bin.zip -P /tmp && \
    unzip /tmp/gradle-8.8-bin.zip -d /opt && \
    ln -s /opt/gradle-8.8/bin/gradle /usr/bin/gradle

# Configure o diretório de trabalho
WORKDIR /app

# Copie o código-fonte do projeto
COPY . .

# Execute a build do Gradle
RUN gradle build --no-daemon

# Use uma imagem base para a execução
FROM openjdk:17-jdk-slim

# Configure o diretório de trabalho
WORKDIR /app

# Copie o JAR construído do estágio anterior
COPY --from=build /app/build/libs/*.jar app.jar

# Exponha a porta 8080
EXPOSE 8081

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
