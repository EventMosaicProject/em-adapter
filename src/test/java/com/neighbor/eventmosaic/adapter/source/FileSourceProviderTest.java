package com.neighbor.eventmosaic.adapter.source;

import com.neighbor.eventmosaic.adapter.exception.FileAccessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты для провайдера файловых источников
 */
class FileSourceProviderTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Проверка доступа к существующему файлу")
    void accessToExistingFile() throws IOException {
        // Arrange
        Path testFile = tempDir.resolve("test.csv");
        String content = "тестовое,содержимое,файла";
        Files.writeString(testFile, content, StandardCharsets.UTF_8);

        FileSourceProvider provider = new LocalFileSourceProvider();

        // Act
        try (InputStream stream = provider.getFileContent(testFile.toString())) {
            assertNotNull(stream, "Поток данных не должен быть null");

            // Assert
            String actualContent = new Scanner(stream, StandardCharsets.UTF_8)
                    .useDelimiter("\\A") // читает все содержимое файла
                    .next();
            assertEquals(content, actualContent, "Содержимое файла должно совпадать");
        }
    }

    @Test
    @DisplayName("Проверка доступа к существующему файлу в ресурсах")
    void accessToResourceFile() throws IOException {
        // Arrange
        FileSourceProvider provider = new LocalFileSourceProvider();

        String path = new ClassPathResource("/data/event_sample.csv").getFile().getAbsolutePath();

        // Act
        try (InputStream stream = provider.getFileContent(path)) {
            assertNotNull(stream, "Поток данных не должен быть null");

            // Assert
            assertTrue(stream.available() > 0, "Файл не должен быть пустым");
        }
    }

    @Test
    @DisplayName("Проверка исключения при доступе к несуществующему файлу")
    void accessToNonExistingFile() {
        // Arrange
        FileSourceProvider provider = new LocalFileSourceProvider();

        // Act & Assert
        assertThrows(FileAccessException.class, () ->
                        provider.getFileContent("/несуществующий/путь/к/файлу.csv"),
                "Должно быть выброшено исключение FileAccessException для несуществующего файла"
        );
    }
}