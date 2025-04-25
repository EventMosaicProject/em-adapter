package com.neighbor.eventmosaic.adapter.source;

import com.neighbor.eventmosaic.adapter.exception.MinioAccessException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * Реализация доступа к файлам в MinIO хранилище.
 * Предоставляет возможность получить содержимое файла в виде
 * потока данных для дальнейшей обработки.
 */
@Slf4j
public class MinioFileSourceProvider implements FileSourceProvider {

    private final MinioClient minioClient;

    /**
     * Создает провайдер с настроенным клиентом MinIO.
     */
    public MinioFileSourceProvider(String url,
                                   String accessKey,
                                   String secretKey) {
        log.info("Инициализация MinIO клиента для сервера: {}", url);
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * Получает содержимое объекта из MinIO хранилища.
     *
     * @param path путь к объекту в формате "bucket/object"
     * @return поток данных для чтения содержимого объекта
     * @throws MinioAccessException если объект не найден или возникла ошибка доступа
     */
    @Override
    public InputStream getFileContent(String path) {
        log.debug("Получение объекта из MinIO: {}", path);
        try {
            // Пока имеем ввиду, что path имеет формат "bucket/object"
            String[] parts = path.split("/", 2);
            String bucket = parts[0];
            String object = parts[1];

            log.debug("Запрос объекта: bucket={}, object={}", bucket, object);
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(object)
                            .build()
            );
            log.debug("Объект успешно получен из MinIO: {}", path);
            return stream;

        } catch (Exception e) {
            log.error("Ошибка при получении файла из MinIO: {}", path, e);
            throw new MinioAccessException("Ошибка при получении файла из MinIO: " + path, e);
        }
    }
}
