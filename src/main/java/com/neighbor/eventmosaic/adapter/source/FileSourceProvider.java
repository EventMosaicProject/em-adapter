package com.neighbor.eventmosaic.adapter.source;

import java.io.InputStream;

/**
 * Интерфейс для получения содержимого файла
 */
public interface FileSourceProvider {

    /**
     * Получает содержимое файла по пути
     *
     * @param path путь к файлу
     * @return содержимое файла
     */
    InputStream getFileContent(String path);
}
