package com.neighbor.eventmosaic.adapter.parser;

import com.neighbor.eventmosaic.library.common.dto.Event;
import com.neighbor.eventmosaic.library.common.dto.Mention;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Тесты для парсеров CSV-файлов
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EventCsvParser.class, MentionCsvParser.class})
class CsvParserTest {

    @Autowired
    private EventCsvParser eventCsvParser;

    @Autowired
    private MentionCsvParser mentionCsvParser;

    @Test
    @DisplayName("Успешный парсинг файла событий")
    void parseEventCsvFile() throws IOException {
        // Arrange
        Resource resource = new ClassPathResource("/data/event_sample.csv");
        InputStream inputStream = resource.getInputStream();

        // Act
        List<Event> events = eventCsvParser.parseStream(inputStream, StandardCharsets.UTF_8);

        // Assert
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertEquals(2, events.size(), "Должно быть 2 события в тестовом файле");

        // Проверка первого события в порядке следования полей в CSV
        Event event = events.getFirst();

        assertNotNull(event.getGlobalEventId());
        assertEquals(1219144758L, event.getGlobalEventId());
        assertEquals(20240107, event.getDay());
        assertEquals(202401, event.getMonthYear());
        assertEquals(2024, event.getYear());
        assertEquals(2024.0192, event.getFractionDate());
        assertNull(event.getActor1Code());
        assertNull(event.getActor1Name());
        assertNull(event.getActor1CountryCode());
        assertNull(event.getActor1KnownGroupCode());
        assertNull(event.getActor1EthnicCode());
        assertNull(event.getActor1Religion1Code());
        assertNull(event.getActor1Religion2Code());
        assertNull(event.getActor1Type1Code());
        assertNull(event.getActor1Type2Code());
        assertNull(event.getActor1Type3Code());
        assertEquals("GOV", event.getActor2Code());
        assertEquals("GOVERNMENT", event.getActor2Name());
        assertNull(event.getActor2CountryCode());
        assertNull(event.getActor2KnownGroupCode());
        assertNull(event.getActor2EthnicCode());
        assertNull(event.getActor2Religion1Code());
        assertNull(event.getActor2Religion2Code());
        assertEquals("GOV", event.getActor2Type1Code());
        assertNull(event.getActor2Type2Code());
        assertNull(event.getActor2Type3Code());
        assertEquals(1, event.getIsRootEvent());
        assertEquals("46", event.getEventCode());
        assertEquals("46", event.getEventBaseCode());
        assertEquals("4", event.getEventRootCode());
        assertEquals(1, event.getQuadClass());
        assertEquals(7.0, event.getGoldsteinScale());
        assertEquals(8, event.getNumMentions());
        assertEquals(1, event.getNumSources());
        assertEquals(8, event.getNumArticles());
        assertEquals(1.78571428571429, event.getAvgTone());
        assertEquals(0, event.getActor1GeoType());
        assertNull(event.getActor1GeoFullName());
        assertNull(event.getActor1GeoCountryCode());
        assertNull(event.getActor1GeoAdm1Code());
        assertNull(event.getActor1GeoAdm2Code());
        assertNull(event.getActor1GeoLat());
        assertNull(event.getActor1GeoLong());
        assertNull(event.getActor1GeoFeatureId());
        assertEquals(1, event.getActor2GeoType());
        assertEquals("Brazil", event.getActor2GeoFullName());
        assertEquals("BR", event.getActor2GeoCountryCode());
        assertEquals("BR", event.getActor2GeoAdm1Code());
        assertNull(event.getActor2GeoAdm2Code());
        assertEquals(-10.0, event.getActor2GeoLat());
        assertEquals(-55.0, event.getActor2GeoLong());
        assertEquals("BR", event.getActor2GeoFeatureId());
        assertEquals(1, event.getActionGeoType());
        assertEquals("Indonesia", event.getActionGeoFullName());
        assertEquals("ID", event.getActionGeoCountryCode());
        assertEquals("ID", event.getActionGeoAdm1Code());
        assertNull(event.getActionGeoAdm2Code());
        assertEquals(-5.0, event.getActionGeoLat());
        assertEquals(120.0, event.getActionGeoLong());
        assertEquals("ID", event.getActionGeoFeatureId());
        assertEquals(20250106214500L, event.getDateAdded());
        assertEquals("https://gate.el-balad.com/64610", event.getSourceUrl());
    }

    @Test
    @DisplayName("Успешный парсинг файла упоминаний")
    void parseMentionCsvFile() throws IOException {
        // Arrange
        Resource resource = new ClassPathResource("/data/mention_sample.csv");
        InputStream inputStream = resource.getInputStream();

        // Act
        List<Mention> mentions = mentionCsvParser.parseStream(inputStream, StandardCharsets.UTF_8);

        // Assert
        assertNotNull(mentions);
        assertFalse(mentions.isEmpty());
        assertEquals(2, mentions.size(), "Должно быть 2 упоминания в тестовом файле");

        // Проверка первого упоминания
        Mention mention = mentions.getFirst();
        assertNotNull(mention.getGlobalEventId(), "GlobalEventId не должен быть null");
        assertEquals(1219110299L, mention.getGlobalEventId());
        assertEquals("dailyhindinews.com", mention.getMentionSourceName());
        assertEquals(20250106223000L, mention.getMentionTimeDate());
        assertEquals(20250106171500L, mention.getEventTimeDate());
        assertEquals(1, mention.getMentionType());
        assertEquals("https://dailyhindinews.com/how-donald-trumps-stance-on-china-increased-indias-tension-a-big-challenge-for-the-modi-government/", mention.getMentionIdentifier());
        assertEquals(6, mention.getSentenceId());
        assertEquals(3512, mention.getActor1CharOffset());
        assertEquals(3483, mention.getActor2CharOffset());
        assertEquals(3557, mention.getActionCharOffset());
        assertEquals(0, mention.getInRawText());
        assertEquals(20, mention.getConfidence());
        assertEquals(4248, mention.getMentionDocLen());
        assertEquals(-2.63157894736842, mention.getMentionDocTone());
        assertEquals("srclc:hin;eng:GT-HIN 1.0", mention.getMentionDocTranslationInfo());
    }

    @Test
    @DisplayName("Фильтрация записей с пустым GlobalEventId в файле событий")
    void filterRecordsWithEmptyGlobalEventId() throws IOException {
        // Arrange
        Resource resource = new ClassPathResource("/data/event_with_missing_ids.csv");
        InputStream inputStream = resource.getInputStream();

        // Act
        List<Event> events = eventCsvParser.parseStream(inputStream, StandardCharsets.UTF_8);

        // Assert
        assertNotNull(events);
        assertEquals(2, events.size(), "Должно быть только 2 события после фильтрации");

        for (Event event : events) {
            assertNotNull(event.getGlobalEventId(), "GlobalEventId не должен быть null после фильтрации");
        }
    }

    @Test
    @DisplayName("Обработка некорректных числовых значений в CSV-файле событий")
    void handleInvalidNumericValuesInEventCsv() throws IOException {
        // Arrange
        Resource resource = new ClassPathResource("/data/event_with_invalid_numeric.csv");
        InputStream inputStream = resource.getInputStream();

        // Act
        List<Event> events = eventCsvParser.parseStream(inputStream, StandardCharsets.UTF_8);

        // Assert
        assertNotNull(events);
        assertFalse(events.isEmpty());

        Event event = events.getFirst();

        assertNotNull(event.getGlobalEventId(), "GlobalEventId должен быть корректно распарсен");
        assertNull(event.getFractionDate(), "Некорректное числовое значение для fractionDate должно быть null");
        assertNull(event.getGoldsteinScale(), "Некорректное числовое значение для goldsteinScale должно быть null");
        assertNull(event.getNumMentions(), "Некорректное числовое значение для numMentions должно быть null");
        assertNull(event.getNumSources(), "Некорректное числовое значение для numSources должно быть null");
        assertNull(event.getAvgTone(), "Некорректное числовое значение для avgTone должно быть null");
    }
}