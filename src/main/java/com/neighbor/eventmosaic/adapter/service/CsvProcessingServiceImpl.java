package com.neighbor.eventmosaic.adapter.service;

import com.neighbor.eventmosaic.adapter.exception.CsvParsingException;
import com.neighbor.eventmosaic.adapter.exception.FileAccessException;
import com.neighbor.eventmosaic.adapter.exception.ParserNotFoundException;
import com.neighbor.eventmosaic.adapter.parser.CsvParser;
import com.neighbor.eventmosaic.adapter.source.FileSourceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Сервис для обработки CSV файлов, получения их содержимого и
 * преобразования в объекты соответствующего типа.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CsvProcessingServiceImpl implements CsvProcessingService {

    private final Map<Class<?>, CsvParser<?>> parsersMap;

    private final FileSourceProvider fileSourceProvider;

    /**
     * Обрабатывает CSV файл, преобразуя его содержимое в список объектов указанного типа.
     *
     * @param path        путь к CSV файлу
     * @param targetClass целевой класс для преобразования
     * @param <T>         тип целевых объектов
     * @return список объектов, созданных из данных CSV
     * @throws ParserNotFoundException если не найден парсер для указанного типа
     * @throws FileAccessException     если не удалось получить доступ к файлу
     * @throws CsvParsingException     если возникла ошибка при парсинге CSV
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> processCsvFile(String path, Class<T> targetClass) {
        log.info("Начало обработки CSV файла: {}, тип данных: {}", path, targetClass.getSimpleName());

        CsvParser<?> parser = parsersMap.get(targetClass);
        if (parser == null) {
            log.error("Парсер не найден для типа: {}", targetClass.getName());
            throw new ParserNotFoundException("Парсер не найден для типа: " + targetClass.getName());
        }

        log.debug("Найден парсер: {}", parser.getClass().getSimpleName());

        try (InputStream is = fileSourceProvider.getFileContent(path)) {
            log.debug("Начало парсинга файла: {}", path);
            List<?> results = parser.parseStream(is, StandardCharsets.UTF_8);
            log.info("Файл {} успешно обработан, получено {} записей", path, results.size());
            return (List<T>) results;

        } catch (FileAccessException e) {
            log.error("Ошибка доступа к файлу: {}", path, e);
            throw e;

        } catch (Exception e) {
            log.error("Ошибка при обработке CSV файла: {}", path, e);
            throw new CsvParsingException("Ошибка при обработке CSV файла: " + path, e);
        }
    }
}
