package com.neighbor.eventmosaic.adapter.exception;

/**
 * Исключение, возникающее при ошибках доступа к локальным файлам.
 * Используется для указания на проблемы с чтением/записью файлов,
 * отсутствие прав доступа или физическое отсутствие файла.
 */
public class FileAccessException extends AdapterException {

    public FileAccessException(String message) {
        super(message);
    }

    public FileAccessException(String message, Throwable cause) {
        super(message, cause);
    }
} 