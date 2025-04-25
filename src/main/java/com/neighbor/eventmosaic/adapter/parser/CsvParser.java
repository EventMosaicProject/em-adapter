package com.neighbor.eventmosaic.adapter.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Интерфейс для парсинга CSV файлов
 */
public interface CsvParser<T> {

    /**
     * Парсит CSV поток в список объектов
     *
     * @param stream  поток с CSV данными
     * @param charset кодировка CSV файла
     * @return список распарсенных объектов
     */
    List<T> parseStream(InputStream stream, Charset charset) throws IOException;

    /**
     * Возвращает класс, с которым работает парсер
     *
     * @return Класс объектов, которые создает парсер
     */
    Class<T> getSupportedClass();
}
