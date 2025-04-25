package com.neighbor.eventmosaic.adapter.exception;

/**
 * Исключение, возникающее когда не найден парсер для указанного типа данных.
 * Обычно указывает на проблему с конфигурацией или отсутствие необходимой
 * реализации в проекте.
 */
public class ParserNotFoundException extends AdapterException {

    public ParserNotFoundException(String message) {
        super(message);
    }
} 