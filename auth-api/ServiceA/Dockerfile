FROM maven:latest AS builder
WORKDIR /app

# Встановлюємо OpenJDK 21 (використовуємо інструкції для Debian, оскільки Maven базується на ньому)
RUN apt-get update && \
    apt-get install -y openjdk-21-jdk && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY pom.xml .
COPY src ./src

# Завантажуємо залежності
RUN mvn dependency:go-offline -B

# Компілюємо та пакуємо у JAR
RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine 
WORKDIR /app

# Копіюємо готовий JAR-файл з BUILDER-стадії
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
