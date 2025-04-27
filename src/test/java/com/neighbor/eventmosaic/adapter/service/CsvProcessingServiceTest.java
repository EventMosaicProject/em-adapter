package com.neighbor.eventmosaic.adapter.service;

import com.neighbor.eventmosaic.adapter.dto.Event;
import com.neighbor.eventmosaic.adapter.dto.Mention;
import com.neighbor.eventmosaic.adapter.exception.CsvParsingException;
import com.neighbor.eventmosaic.adapter.exception.FileAccessException;
import com.neighbor.eventmosaic.adapter.exception.ParserNotFoundException;
import com.neighbor.eventmosaic.adapter.parser.CsvParser;
import com.neighbor.eventmosaic.adapter.parser.EventCsvParser;
import com.neighbor.eventmosaic.adapter.parser.MentionCsvParser;
import com.neighbor.eventmosaic.adapter.source.FileSourceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты для сервиса обработки CSV-файлов
 */
@ExtendWith(MockitoExtension.class)
class CsvProcessingServiceTest {

    @Mock
    private FileSourceProvider fileSourceProvider;

    @Mock
    private EventCsvParser eventCsvParser;

    @Mock
    private MentionCsvParser mentionCsvParser;

    private CsvProcessingService csvProcessingService;

    @BeforeEach
    void setUp() {
        Map<Class<?>, CsvParser<?>> parsersMap = new HashMap<>() {{
            put(Event.class, eventCsvParser);
            put(Mention.class, mentionCsvParser);
        }};

        csvProcessingService = new CsvProcessingServiceImpl(parsersMap, fileSourceProvider);
    }

    @Test
    @DisplayName("Успешная обработка CSV-файла с событиями")
    void shouldSuccessfullyProcessEventsCsvFile() throws IOException {
        // Arrange
        String path = Path.of("C:/path/to/event.csv").toString();
        Event event = new Event();
        event.setGlobalEventId(123456789L);

        mockFileContent(path);
        when(eventCsvParser.parseStream(any(InputStream.class), any())).thenReturn(List.of(event));

        // Act
        List<Event> result = csvProcessingService.processCsvFile(path, Event.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(123456789L, result.getFirst().getGlobalEventId());

        verify(fileSourceProvider).getFileContent(path);
        verify(eventCsvParser).parseStream(any(InputStream.class), eq(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Успешная обработка CSV-файла с упоминаниями")
    void shouldSuccessfullyProcessMentionsCsvFile() throws IOException {
        // Arrange
        String path = Path.of("C:/path/to/mention.csv").toString();
        Mention mention = new Mention();
        mention.setGlobalEventId(123456789L);

        mockFileContent(path);
        when(mentionCsvParser.parseStream(any(InputStream.class), any())).thenReturn(List.of(mention));

        // Act
        List<Mention> result = csvProcessingService.processCsvFile(path, Mention.class);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(123456789L, result.getFirst().getGlobalEventId());

        verify(fileSourceProvider).getFileContent(path);
        verify(mentionCsvParser).parseStream(any(InputStream.class), eq(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Исключение при отсутствии парсера для указанного типа")
    void shouldThrowParserNotFoundException() {
        // Act & Assert
        assertThrows(ParserNotFoundException.class, () ->
                        csvProcessingService.processCsvFile("path", String.class),
                "Должно быть выброшено исключение ParserNotFoundException для неподдерживаемого типа"
        );
    }

    @Test
    @DisplayName("Исключение при ошибке доступа к файлу")
    void shouldThrowFileAccessException() {
        // Arrange
        String path = Path.of("C:/path/to/file.csv").toString();
        when(fileSourceProvider.getFileContent(path)).thenThrow(new FileAccessException("Ошибка доступа", new IOException()));

        // Act & Assert
        assertThrows(FileAccessException.class, () ->
                        csvProcessingService.processCsvFile(path, Event.class),
                "Должно быть выброшено исключение FileAccessException при ошибке доступа к файлу"
        );
    }

    @Test
    @DisplayName("Исключение при ошибке парсинга CSV")
    void shouldThrowCsvParsingException() throws IOException {
        // Arrange
        String path = Path.of("C:/path/to/file.csv").toString();

        mockFileContent(path);
        when(eventCsvParser.parseStream(any(InputStream.class), any())).thenThrow(new IOException("Ошибка парсинга"));

        // Act & Assert
        assertThrows(CsvParsingException.class, () ->
                        csvProcessingService.processCsvFile(path, Event.class),
                "Должно быть выброшено исключение CsvParsingException при ошибке парсинга CSV"
        );
    }

    /**
     * Мокирует содержимое файла
     *
     * @param path путь к файлу
     */
    private void mockFileContent(String path) {
        InputStream is = new ByteArrayInputStream("test data".getBytes(StandardCharsets.UTF_8));
        when(fileSourceProvider.getFileContent(path)).thenReturn(is);
    }

}