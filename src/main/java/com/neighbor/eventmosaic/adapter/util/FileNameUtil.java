package com.neighbor.eventmosaic.adapter.util;

import lombok.experimental.UtilityClass;

import java.nio.file.Paths;

/**
 * Утилитный класс для работы с именами файлов
 */
@UtilityClass
public class FileNameUtil {

    /**
     * Извлекает идентификатор батча (временную метку) из имени файла GDELT
     * Например, из "20250323151500.translation.export.CSV" вернет "20250323151500"
     */
    public static String extractBatchId(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        String[] parts = fileName.split("\\.");
        return parts.length > 0
                ? parts[0]
                : null;
    }
}