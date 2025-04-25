package com.neighbor.eventmosaic.adapter.config;

import com.neighbor.eventmosaic.adapter.source.FileSourceProvider;
import com.neighbor.eventmosaic.adapter.source.LocalFileSourceProvider;
import com.neighbor.eventmosaic.adapter.source.MinioFileSourceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Конфигурация для провайдеров файлов.
 * В профиле "dev" используется локальный доступ к файлам,
 * а в профиле "prod" - доступ к файлам в MinIO хранилище.
 */
@Configuration
public class FileSourceConfig {

    @Bean
    @Profile("dev")
    public FileSourceProvider localFileSourceProvider() {
        return new LocalFileSourceProvider();
    }

    @Bean
    @Profile("prod")
    public FileSourceProvider minioFileSourceProvider(
            @Value("${storage.minio.url}") String url,
            @Value("${storage.minio.access-key}") String accessKey,
            @Value("${storage.minio.secret-key}") String secretKey) {
        return new MinioFileSourceProvider(url, accessKey, secretKey);
    }
}
