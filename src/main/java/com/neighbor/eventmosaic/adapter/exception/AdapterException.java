package com.neighbor.eventmosaic.adapter.exception;

/**
 * Базовое исключение для всех специфичных ошибок в адаптере.
 * Служит основой для иерархии исключений приложения, позволяя
 * единообразно обрабатывать ошибки на верхних уровнях.
 */
public class AdapterException extends RuntimeException {


    public AdapterException(String message) {
        super(message);
    }

    public AdapterException(String message, Throwable cause) {
        super(message, cause);
    }
} 