package com.neighbor.eventmosaic.adapter.source;

import com.neighbor.eventmosaic.adapter.exception.FileAccessException;
import com.neighbor.eventmosaic.adapter.exception.MinioAccessException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Реализация доступа к файлам в MinIO хранилище по URL.
 * Предоставляет возможность получить содержимое файла в виде
 * потока данных для дальнейшей обработки.
 */
@Slf4j
@RequiredArgsConstructor
public class MinioFileSourceProvider implements FileSourceProvider {

    private final MinioClient minioClient;

    /**
     * Получает содержимое объекта из MinIO хранилища по его URL.
     *
     * @param fileUrl URL объекта в MinIO (например, "http://minio:9000/event-mosaic/data/file.csv")
     * @return поток данных для чтения содержимого объекта
     * @throws FileAccessException если URL некорректен, объект не найден или возникла ошибка доступа
     */
    @Override
    public InputStream getFileContent(String fileUrl) {
        log.debug("Получение объекта из MinIO по URL: {}", fileUrl);
        try {
            String[] minioPathComponents = parseMinioUrl(fileUrl);
            String bucket = minioPathComponents[0];
            String object = minioPathComponents[1];

            log.debug("Запрос объекта: bucket='{}', object='{}'", bucket, object);
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(object)
                            .build()
            );
            log.debug("Объект успешно получен из MinIO по URL: {}", fileUrl);
            return stream;

        } catch (URISyntaxException e) {
            log.error("Некорректный синтаксис URL MinIO: {}", fileUrl, e);
            throw new FileAccessException("Некорректный URL файла MinIO: " + fileUrl, e);
        } catch (IllegalArgumentException e) {
            throw new FileAccessException("Ошибка парсинга URL файла MinIO: " + fileUrl, e);
        } catch (Exception e) {
            log.error("Ошибка при получении файла из MinIO по URL: {}", fileUrl, e);
            throw new MinioAccessException("Ошибка при получении файла из MinIO: " + fileUrl, e);
        }
    }

    /**
     * Разбирает URL MinIO на имя бакета и путь к объекту.
     *
     * @param fileUrl URL файла в MinIO.
     * @return Массив строк, где первый элемент - имя бакета, второй - имя объекта.
     * @throws URISyntaxException       если URL имеет неверный синтаксис.
     * @throws IllegalArgumentException если путь в URL некорректен или не содержит бакет и объект.
     */
    private String[] parseMinioUrl(String fileUrl) throws URISyntaxException, IllegalArgumentException {
        URI uri = new URI(fileUrl);
        String path = uri.getPath(); // Например /event-mosaic/data/file.csv

        if (Objects.isNull(path) || path.length() <= 1 || !path.startsWith("/")) {
            log.error("Некорректный путь в URL MinIO: '{}' для URL: '{}'", path, fileUrl);
            throw new IllegalArgumentException("Некорректный путь в URL MinIO: " + path);
        }

        // Убираем ведущий /
        String fullPath = path.substring(1);
        // Разделяем по первому /
        String[] parts = fullPath.split("/", 2);

        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            log.error("URL MinIO '{}' не содержит корректное имя бакета и/или объекта. Path: '{}', Parts: {}",
                    fileUrl,
                    fullPath,
                    Arrays.toString(parts));
            throw new IllegalArgumentException("URL MinIO не содержит имя бакета и/или объекта: " + fileUrl);
        }

        return parts; // parts[0] - bucket, parts[1] - object
    }
}
