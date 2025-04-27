package com.neighbor.eventmosaic.adapter.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

/**
 * Утилитный класс для парсинга CSV-файлов
 */
@Slf4j
@UtilityClass
public class CsvParserUtil {

    /**
     * Безопасно получает строковое значение из записи
     */
    public static String getString(CSVRecord csvRecord, int index) {
        if (index >= csvRecord.size()) {
            return null;
        }

        String value = csvRecord.get(index);
        return StringUtils.isEmpty(value)
                ? null
                : value;
    }

    /**
     * Безопасно получает и преобразует значение в Long
     */
    public static Long getLong(CSVRecord csvRecord, int index) {
        return getAndConvert(csvRecord, index, Long::parseLong);
    }

    /**
     * Безопасно получает и преобразует значение в Integer
     */
    public static Integer getInteger(CSVRecord csvRecord, int index) {
        return getAndConvert(csvRecord, index, Integer::parseInt);
    }

    /**
     * Безопасно получает и преобразует значение в Double
     */
    public static Double getDouble(CSVRecord csvRecord, int index) {
        return getAndConvert(csvRecord, index, Double::parseDouble);
    }

    /**
     * Обобщенный метод для безопасного получения и преобразования значения.
     *
     * @param csvRecord CSV-запись
     * @param index     индекс колонки
     * @param converter функция преобразования строки в значение типа T
     * @return значение типа T или null, если индекс выходит за пределы диапазона или строка пуста
     */
    private static <T> T getAndConvert(CSVRecord csvRecord,
                                       int index,
                                       Function<String, T> converter) {
        if (index >= csvRecord.size()) {
            return null;
        }

        String value = csvRecord.get(index);
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        try {
            return converter.apply(value);
        } catch (Exception e) {
            log.error("Ошибка при преобразовании значения: {}", e.getMessage());
            return null;
        }
    }
}
