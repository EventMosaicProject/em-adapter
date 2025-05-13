package com.neighbor.eventmosaic.adapter.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Конфигурационные свойства для подключения к MinIO.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "storage.minio")
public class MinioProperties {

    /**
     * URL эндпоинта MinIO сервера.
     * Пример: http://minio:9000
     */
    private String endpoint;

    /**
     * Ключ доступа (Access Key) для аутентификации в MinIO.
     */
    private String accessKey;

    /**
     * Секретный ключ (Secret Key) для аутентификации в MinIO.
     */
    private String secretKey;
}