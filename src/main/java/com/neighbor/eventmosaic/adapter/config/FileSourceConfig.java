package com.neighbor.eventmosaic.adapter.config;

import com.neighbor.eventmosaic.adapter.config.properties.MinioProperties;
import com.neighbor.eventmosaic.adapter.source.FileSourceProvider;
import com.neighbor.eventmosaic.adapter.source.MinioFileSourceProvider;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация для провайдеров файлов.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class FileSourceConfig {

    /**
     * Этот клиент будет использоваться MinioFileSourceProvider.
     *
     * @param minioProperties Конфигурационные свойства MinIO.
     * @return Сконфигурированный MinioClient.
     */
    @Bean
    public MinioClient minioClient(MinioProperties minioProperties) {
        log.info("Инициализация MinIO клиента. Endpoint: '{}'", minioProperties.getEndpoint());
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(minioProperties.getEndpoint())
                    .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                    .build();
            log.info("MinIO клиент успешно инициализирован для endpoint: {}", minioProperties.getEndpoint());
            return client;

        } catch (Exception e) {
            log.error("Критическая ошибка: Не удалось инициализировать MinIO клиент для endpoint: {}. Причина: {}",
                    minioProperties.getEndpoint(), e.getMessage(), e);
            throw new IllegalStateException("Сбой инициализации MinIO клиента", e);
        }
    }

    @Bean
    public FileSourceProvider minioFileSourceProvider(MinioClient minioClient) {
        return new MinioFileSourceProvider(minioClient);
    }
}
