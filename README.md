# Сервис преобразования данных (em-adapter)

Микросервис `em-adapter` отвечает за получение путей к CSV-файлам из Kafka, их обработку, преобразование в объекты доменной модели и отправку этих объектов в соответствующие топики Kafka для дальнейшей обработки em-processor.

## Основной рабочий процесс

1. **Получение пути к файлу из Kafka:**
   * Сервис слушает топики Kafka (`gdelt-collector-event-topic` и `gdelt-collector-mention-topic`), которые содержат URL к CSV-файлам в MinIO хранилище в формате строки.
   * Архитектура сервиса использует `MinioFileSourceProvider` для получения файлов из MinIO по предоставленному URL.

2. **Доступ к файлу:**
   * Сервис использует реализацию `MinioFileSourceProvider` для доступа к файлам в MinIO хранилище по URL, полученному из Kafka.

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

## Расширяемость

* **Абстракция источника файлов:** Интерфейс `FileSourceProvider` позволяет легко добавить новые источники данных помимо MinIO.
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

    Kafka->>Listener: Сообщение с URL к файлу в MinIO
    Note over Listener: Определение типа файла<br>по имени (если применимо) или типу события Kafka

    alt Файл с событиями (*.export.CSV)
        Listener->>CsvService: processCsvFile(fileUrl, Event.class)
        CsvService->>FileProvider: getFileContent(fileUrl)
        FileProvider->>FileProvider: Получить объект из MinIO по URL
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
        Listener->>CsvService: processCsvFile(fileUrl, Mention.class)
        CsvService->>FileProvider: getFileContent(fileUrl)
        FileProvider->>FileProvider: Получить объект из MinIO по URL
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
