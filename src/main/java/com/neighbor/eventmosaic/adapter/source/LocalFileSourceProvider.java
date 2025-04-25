package com.neighbor.eventmosaic.adapter.source;

import com.neighbor.eventmosaic.adapter.exception.FileAccessException;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Реализация доступа к локальным файлам по абсолютному пути.
 * Предоставляет возможность получить содержимое файла в виде
 * потока данных для дальнейшей обработки.
 */
@Slf4j
public class LocalFileSourceProvider implements FileSourceProvider {

    /**
     * Получает содержимое файла в виде потока данных.
     *
     * @param path абсолютный путь к файлу в локальной файловой системе
     * @return поток данных для чтения содержимого файла
     * @throws FileAccessException если файл не найден или возникла ошибка доступа
     */
    @Override
    public InputStream getFileContent(String path) {
        log.debug("Получение содержимого файла: {}", path);
        try {
            InputStream stream = new FileInputStream(path);
            log.debug("Файл успешно открыт: {}", path);
            return stream;

        } catch (IOException e) {
            log.error("Ошибка при открытии файла: {}", path, e);
            throw new FileAccessException("Ошибка при открытии файла: " + path, e);
        }
    }
}
