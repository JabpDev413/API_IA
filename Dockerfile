# Estágio de Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Estágio de Execução
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build target/*.jar app.jar

# 🔑 ESTE É O SEGREDO: Mapeia as variáveis do painel do Render para dentro do Java
ARG GROQ_API_KEY
ARG COHERE_API_KEY
ENV GROQ_API_KEY=$GROQ_API_KEY
ENV COHERE_API_KEY=$COHERE_API_KEY

EXPOSE 6060
ENTRYPOINT ["java", "-jar", "app.jar"]