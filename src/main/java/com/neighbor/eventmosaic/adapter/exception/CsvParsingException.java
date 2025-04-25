package com.neighbor.eventmosaic.adapter.exception;

/**
 * Исключение, возникающее при ошибках парсинга CSV-файлов.
 * Используется для указания на проблемы с форматом данных,
 * невалидные значения или несовместимость схемы данных.
 */
public class CsvParsingException extends AdapterException {

    public CsvParsingException(String message) {
        super(message);
    }

    public CsvParsingException(String message, Throwable cause) {
        super(message, cause);
    }
} 