FROM openjdk:21-jdk-slim AS builder

# Устанавливаем корневую рабочую директорию для сборки.
# Помогает избежать путаницы с путями.
WORKDIR /build_root

# Копируем общую библиотеку em-library-common.
# Путь em-library-common указывается относительно контекста сборки Docker,
# который является корневой директорией проекта (например, EventMosaic/).
COPY em-library-common/ /build_root/em-library-common/

# Копируем файлы проекта и настройки Gradle для em-adapter.
# Пути также указываются относительно контекста сборки.
COPY em-adapter/gradlew /build_root/em-adapter/gradlew
COPY em-adapter/gradle /build_root/em-adapter/gradle
COPY em-adapter/build.gradle.kts /build_root/em-adapter/build.gradle.kts
COPY em-adapter/settings.gradle.kts /build_root/em-adapter/settings.gradle.kts
COPY em-adapter/gradle.properties /build_root/em-adapter/gradle.properties

# Копируем исходный код приложения em-adapter.
COPY em-adapter/src/ /build_root/em-adapter/src/

# Переходим в рабочую директорию сервиса em-adapter внутри контейнера.
WORKDIR /build_root/em-adapter

# Собираем jar-файл
# Файл settings.gradle.kts (в /build_root/em-adapter/settings.gradle.kts)
# с includeBuild("../em-library-common") корректно найдет библиотеку
# по пути /build_root/em-library-common/.
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

# Этап формирования финального образа для рантайма
FROM openjdk:21-jdk-slim
WORKDIR /app

# Создаем директорию для логов
RUN mkdir -p /app/logs && chmod 777 /app/logs

# Копируем собранный jar-файл из этапа сборки.
# Путь к артефакту должен учитывать структуру в builder stage.
COPY --from=builder /build_root/em-adapter/build/libs/*.jar app.jar

EXPOSE 8080

# Запуск приложения
CMD ["java", "-jar", "app.jar"]


# ========== Конфигурация при использовании внешнего хранилища для общей либы ==========

# Этап сборки приложения Adapter
#FROM openjdk:21-jdk-slim AS builder

#WORKDIR /app

# Копируем файлы проекта и настройки Gradle
#COPY gradlew .
#COPY gradle gradle
#COPY build.gradle.kts .
#COPY settings.gradle.kts .
#COPY gradle.properties .

# Копируем исходный код приложения
#COPY src src

# Собираем jar-файл приложения
#RUN chmod +x gradlew && ./gradlew bootJar

# Этап формирования финального образа для рантайма
#FROM openjdk:21-jdk-slim
#WORKDIR /app

# Создаем директорию для логов
#RUN mkdir -p /app/logs && chmod 777 /app/logs

# Копируем собранный jar-файл
#COPY --from=builder /app/build/libs/*.jar app.jar

#EXPOSE 8080

# Запуск приложения
#CMD ["java", "-jar", "app.jar"]