package com.neighbor.eventmosaic.adapter.exception;

/**
 * Исключение, возникающее при ошибках доступа к объектам в MinIO.
 * Используется для указания на проблемы с подключением к хранилищу,
 * отсутствующие бакеты/объекты или ошибки аутентификации.
 */
public class MinioAccessException extends AdapterException {

    public MinioAccessException(String message) {
        super(message);
    }

    public MinioAccessException(String message, Throwable cause) {
        super(message, cause);
    }
} 