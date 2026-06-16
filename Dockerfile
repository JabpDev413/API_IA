# Estágio de Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Estágio de Execução
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build target/*.jar app.jar

# 🔑 ESTE É O SEGREDO: Mapeia as variáveis do painel do Render para dentro do Java
ARG GCP_API_KEY
ARG HUGGINGFACE_API_KEY
ENV GCP_API_KEY=$GCP_API_KEY
ENV HUGGINGFACE_API_KEY=$HUGGINGFACE_API_KEY

EXPOSE 6060
ENTRYPOINT ["java", "-jar", "app.jar"]