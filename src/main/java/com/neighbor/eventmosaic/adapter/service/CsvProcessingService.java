package com.neighbor.eventmosaic.adapter.service;

import java.util.List;

/**
 * Сервис для обработки CSV-файлов
 */
public interface CsvProcessingService {

    /**
     * Обрабатывает CSV-файл и возвращает список объектов заданного типа
     *
     * @param path        путь к CSV-файлу
     * @param targetClass класс, в который будут преобразованы данные
     * @return список объектов заданного типа
     */
    <T> List<T> processCsvFile(String path, Class<T> targetClass);
}
