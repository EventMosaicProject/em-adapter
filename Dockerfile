# Этап сборки приложения Adapter
FROM openjdk:21-jdk-slim AS builder

WORKDIR /app

# Копируем файлы проекта и настройки Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .

# Копируем исходный код приложения
COPY src src

# Собираем jar-файл приложения
RUN chmod +x gradlew && ./gradlew bootJar

# Этап формирования финального образа для рантайма
FROM openjdk:21-jdk-slim
WORKDIR /app

# Создаем директорию для логов
RUN mkdir -p /app/logs && chmod 777 /app/logs

# Копируем собранный jar-файл
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8082

# Запуск приложения
CMD ["java", "-jar", "app.jar"]