package com.neighbor.eventmosaic.adapter.source;

import com.neighbor.eventmosaic.adapter.exception.FileAccessException;
import com.neighbor.eventmosaic.adapter.exception.MinioAccessException;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты для MinioFileSourceProvider.
 * Проверяют логику парсинга URL и взаимодействие с MinioClient.
 */
@ExtendWith(MockitoExtension.class)
class FileSourceProviderTest {

    @Mock
    private MinioClient minioClient;

    private MinioFileSourceProvider provider;

    @BeforeEach
    void setUp() {
        provider = new MinioFileSourceProvider(minioClient);
    }

    @Test
    @DisplayName("Успешное получение контента по валидному MinIO URL")
    void getFileContent_validMinioUrl_returnsContent() throws Exception {
        // Arrange
        String fileUrl = "http://minio.example.com:9000/my-bucket/my-object.csv";
        String expectedContent = "id,value\n1,test data";

        final InputStream actualDataStream = new ByteArrayInputStream(expectedContent.getBytes(StandardCharsets.UTF_8));

        GetObjectResponse mockedResponse = mock(GetObjectResponse.class);

        when(mockedResponse.read(any(byte[].class), anyInt(), anyInt()))
                .thenAnswer(invocation -> {
                    byte[] buffer = invocation.getArgument(0);
                    int offset = invocation.getArgument(1);
                    int length = invocation.getArgument(2);
                    return actualDataStream.read(buffer, offset, length);
                });

        doAnswer(invocation -> {
            actualDataStream.close();
            return null;
        }).when(mockedResponse).close();

        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockedResponse);

        // Act
        try (InputStream stream = provider.getFileContent(fileUrl)) {
            assertNotNull(stream, "Поток данных не должен быть null");

            String actualContent = new Scanner(stream, StandardCharsets.UTF_8)
                    .useDelimiter("\\A")
                    .next();
            assertEquals(expectedContent, actualContent, "Содержимое файла должно совпадать");
        }

        ArgumentCaptor<GetObjectArgs> argsCaptor = ArgumentCaptor.forClass(GetObjectArgs.class);
        verify(minioClient).getObject(argsCaptor.capture());
        GetObjectArgs capturedArgs = argsCaptor.getValue();
        assertEquals("my-bucket", capturedArgs.bucket());
        assertEquals("my-object.csv", capturedArgs.object());
    }

    @Test
    @DisplayName("Исключение FileAccessException при некорректном синтаксисе URL")
    void getFileContent_invalidUrlSyntax_throwsFileAccessException() {
        // Arrange
        String invalidUrl = "http://example.com/path with spaces"; // Некорректный URL с пробелами для URISyntaxException

        // Act & Assert
        FileAccessException exception = assertThrows(FileAccessException.class, () ->
                        provider.getFileContent(invalidUrl),
                "Должно быть выброшено исключение FileAccessException для URL с неверным синтаксисом"
        );
        assertTrue(exception.getMessage().contains("Некорректный URL файла MinIO"));
        assertNotNull(exception.getCause());
        assertInstanceOf(URISyntaxException.class, exception.getCause());
    }

    @Test
    @DisplayName("Исключение FileAccessException при URL без указания объекта")
    void getFileContent_urlMissingObject_throwsFileAccessException() {
        // Arrange
        String incompleteUrl = "http://minio.example.com:9000/my-bucket/"; // Только бакет, нет объекта

        // Act & Assert
        FileAccessException exception = assertThrows(FileAccessException.class, () ->
                        provider.getFileContent(incompleteUrl),
                "Должно быть выброшено исключение FileAccessException для URL без объекта"
        );
        assertTrue(exception.getMessage().contains("Ошибка парсинга URL файла MinIO"));
        assertNotNull(exception.getCause());
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    }

    @Test
    @DisplayName("Исключение FileAccessException при URL, где не удается выделить бакет")
    void getFileContent_urlCannotParseBucket_throwsFileAccessException() {
        // Arrange
        String urlNoBucketInPath = "http://minio.example.com/object.csv";

        // Act & Assert
        FileAccessException exception = assertThrows(FileAccessException.class, () ->
                        provider.getFileContent(urlNoBucketInPath),
                "Должно быть выброшено исключение FileAccessException, если бакет не может быть корректно извлечен"
        );
        assertTrue(exception.getMessage().contains("Ошибка парсинга URL файла MinIO"));
        assertNotNull(exception.getCause());
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    }

    @Test
    @DisplayName("Исключение MinioAccessException при ошибке клиента MinIO (например, объект не найден)")
    void getFileContent_minioClientError_throwsMinioAccessException() throws Exception {
        // Arrange
        String fileUrl = "http://minio.example.com:9000/my-bucket/non-existing-object.csv";

        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenThrow(new IOException("Смоделированная ошибка Minio SDK: объект не найден"));

        // Act & Assert
        MinioAccessException exception = assertThrows(MinioAccessException.class, () ->
                        provider.getFileContent(fileUrl),
                "Должно быть выброшено исключение MinioAccessException при ошибке со стороны MinIO"
        );
        assertTrue(exception.getMessage().contains("Ошибка при получении файла из MinIO"));
        assertNotNull(exception.getCause());
        assertInstanceOf(IOException.class, exception.getCause());
    }
}