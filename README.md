# Сервис преобразования данных (em-adapter)

Микросервис `em-adapter` отвечает за получение путей к CSV-файлам из Kafka, их обработку, преобразование в объекты доменной модели и отправку этих объектов в соответствующие топики Kafka для дальнейшей обработки em-processor.

## Основной рабочий процесс

1. **Получение пути к файлу из Kafka:**
    * Сервис слушает топики Kafka (`gdelt-collector-event-topic` и `gdelt-collector-mention-topic`), которые содержат пути к CSV-файлам в формате строки.
    * На данный момент используются абсолютные пути к файлам на локальной файловой системе, но архитектура подготовлена для работы с другими источниками файлов (например, MinIO).

2. **Доступ к файлу:**
    * В зависимости от конфигурации, сервис использует соответствующую реализацию `FileSourceProvider`:
        * `LocalFileSourceProvider` - для доступа к файлам по абсолютному пути на локальной файловой системе
        * `MinioFileSourceProvider` - для доступа к файлам в MinIO хранилище

3. **Парсинг CSV-файла:**
    * `CsvProcessingService` получает содержимое файла через `FileSourceProvider` и передает поток данных соответствующему парсеру.
    * В зависимости от типа файла (определяемого по имени) используется один из специализированных парсеров:
        * `EventCsvParser` - для файлов GDELT с событиями (`*.translation.export.CSV`)
        * `MentionCsvParser` - для файлов GDELT с упоминаниями (`*.translation.mentions.CSV`)
    * Парсеры используют библиотеку Apache Commons CSV для потоковой обработки CSV-файлов, что позволяет эффективно обрабатывать файлы большого размера.

4. **Преобразование и валидация данных:**
    * CSV-записи преобразуются в соответствующие объекты (`Event` или `Mention`).
    * Выполняется базовая валидация данных — проверяется наличие ключевых полей (например, `GlobalEventId`).
    * Невалидные записи логируются и пропускаются, но процесс обработки продолжается.

5. **Отправка в Kafka:**
    * Преобразованные объекты сериализуются в JSON и отправляются в соответствующие исходящие топики Kafka:
        * События (`Event`) => `gdelt-adapter-event-topic`
        * Упоминания (`Mention`) => `gdelt-adapter-mention-topic`
    * Для надежной доставки используется настройка `acks=all` и механизм идемпотентности.

## Обработка ошибок

* **Ошибки доступа к файлам:** Обрабатываются через специализированные исключения `FileAccessException` и `MinioAccessException`.
* **Ошибки парсинга:** При проблемах с форматом данных выбрасывается `CsvParsingException`.
* **Ошибки Kafka:** Настроен `DefaultErrorHandler` с политикой повторных попыток (`FixedBackOff`) для обработки временных сбоев.
* **Валидация данных:** Невалидные записи с отсутствующими ключевыми полями логируются и пропускаются.

## Расширяемость

* **Абстракция источника файлов:** Интерфейс `FileSourceProvider` позволяет легко добавить новые источники данных помимо локальной файловой системы и MinIO.
* **Абстракция парсеров:** Интерфейс `CsvParser<T>` обеспечивает единообразную обработку различных типов CSV-файлов.
* **Масштабируемость:** Потоковая обработка CSV и пакетная отправка в Kafka позволяют эффективно обрабатывать большие объемы данных.

## Диаграмма последовательности (клик на кнопку ⟷ развернет схему)

```mermaid
sequenceDiagram
    participant Kafka as Kafka Consumer
    participant Listener as KafkaListener
    participant FileProvider as FileSourceProvider
    participant CsvService as CsvProcessingService
    participant Parser as CsvParser
    participant Producer as KafkaProducer
    participant OutKafka as Kafka Topics

    Kafka->>Listener: Сообщение с путем к файлу
    Note over Listener: Определение типа файла<br>по имени

    alt Файл с событиями (*.export.CSV)
        Listener->>CsvService: processCsvFile(path, Event.class)
        CsvService->>FileProvider: getFileContent(path)
        alt Локальный файл
            FileProvider->>FileProvider: Открыть локальный файл
        else MinIO
            FileProvider->>FileProvider: Получить объект из MinIO
        end
        FileProvider-->>CsvService: InputStream
        CsvService->>Parser: EventCsvParser.parseStream()
        Parser->>Parser: Чтение и парсинг CSV
        
        loop Для каждой CSV-записи
            Parser->>Parser: Преобразование в Event
            alt GlobalEventId не null
                Parser->>Parser: Добавить в результат
            else GlobalEventId null
                Parser->>Parser: Логирование и пропуск
            end
        end
        
        Parser-->>CsvService: List<Event>
        CsvService-->>Listener: List<Event>
        
        loop Для каждого Event
            Listener->>Producer: Отправка в gdelt-adapter-event-topic
        end
        Producer->>OutKafka: Event в JSON-формате
        OutKafka-->>Producer: ACK
        
    else Файл с упоминаниями (*.mentions.CSV)
        Listener->>CsvService: processCsvFile(path, Mention.class)
        CsvService->>FileProvider: getFileContent(path)
        FileProvider-->>CsvService: InputStream
        CsvService->>Parser: MentionCsvParser.parseStream()
        Parser->>Parser: Чтение и парсинг CSV
        
        loop Для каждой CSV-записи
            Parser->>Parser: Преобразование в Mention
            alt GlobalEventId не null
                Parser->>Parser: Добавить в результат
            else GlobalEventId null
                Parser->>Parser: Логирование и пропуск
            end
        end
        
        Parser-->>CsvService: List<Mention>
        CsvService-->>Listener: List<Mention>
        
        loop Для каждого Mention
            Listener->>Producer: Отправка в gdelt-adapter-mention-topic
        end
        Producer->>OutKafka: Mention в JSON-формате
        OutKafka-->>Producer: ACK
    end
    
    alt Ошибка на любом этапе
        Note over Listener: Логирование ошибки
        Note over Listener: Повторная попытка<br>(до max-retry-attempts раз)
    end
```
