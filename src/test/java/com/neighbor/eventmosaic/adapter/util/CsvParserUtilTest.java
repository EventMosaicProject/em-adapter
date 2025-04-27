package com.neighbor.eventmosaic.adapter.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Тесты для CsvParserUtil
 */
class CsvParserUtilTest {

    @Test
    @DisplayName("Получение строкового значения из CSV-записи")
    void shouldGetString() throws IOException {
        // Arrange
        String csvData = "value1,value2,value3";
        CSVRecord csvRecord = parseRecord(csvData);

        // Act & Assert
        assertEquals("value1", CsvParserUtil.getString(csvRecord, 0));
        assertEquals("value2", CsvParserUtil.getString(csvRecord, 1));
        assertEquals("value3", CsvParserUtil.getString(csvRecord, 2));
        assertNull(CsvParserUtil.getString(csvRecord, 3));
    }

    @Test
    @DisplayName("Обработка пустых значений в getString")
    void shouldGetStringWithEmptyValues() throws IOException {
        // Arrange
        String csvData = "value1,,value3";
        CSVRecord csvRecord = parseRecord(csvData);

        // Act & Assert
        assertEquals("value1", CsvParserUtil.getString(csvRecord, 0));
        assertNull(CsvParserUtil.getString(csvRecord, 1));
        assertEquals("value3", CsvParserUtil.getString(csvRecord, 2));
    }

    @Test
    @DisplayName("Получение целочисленного значения из CSV-записи")
    void shouldGetInteger() throws IOException {
        // Arrange
        String csvData = "123,456,789";
        CSVRecord csvRecord = parseRecord(csvData);

        // Act & Assert
        assertEquals(123, CsvParserUtil.getInteger(csvRecord, 0));
        assertEquals(456, CsvParserUtil.getInteger(csvRecord, 1));
        assertEquals(789, CsvParserUtil.getInteger(csvRecord, 2));
        assertNull(CsvParserUtil.getInteger(csvRecord, 3));
    }

    @Test
    @DisplayName("Обработка невалидных целочисленных значений")
    void shouldGetIntegerWithInvalidValues() throws IOException {
        // Arrange
        String csvData = "123,abc,456";
        CSVRecord csvRecord = parseRecord(csvData);

        // Act & Assert
        assertEquals(123, CsvParserUtil.getInteger(csvRecord, 0));
        assertNull(CsvParserUtil.getInteger(csvRecord, 1));
        assertEquals(456, CsvParserUtil.getInteger(csvRecord, 2));
    }

    @Test
    @DisplayName("Получение значения типа Long из CSV-записи")
    void shouldGetLong() throws IOException {
        // Arrange
        String csvData = "123456789012345,987654321098765";
        CSVRecord csvRecord = parseRecord(csvData);

        // Act & Assert
        assertEquals(123456789012345L, CsvParserUtil.getLong(csvRecord, 0));
        assertEquals(987654321098765L, CsvParserUtil.getLong(csvRecord, 1));
        assertNull(CsvParserUtil.getLong(csvRecord, 2));
    }

    @Test
    @DisplayName("Обработка невалидных значений типа Long")
    void shouldGetLongWithInvalidValues() throws IOException {
        // Arrange
        String csvData = "123456789012345,abc,987654321098765";
        CSVRecord csvRecord = parseRecord(csvData);

        // Act & Assert
        assertEquals(123456789012345L, CsvParserUtil.getLong(csvRecord, 0));
        assertNull(CsvParserUtil.getLong(csvRecord, 1));
        assertEquals(987654321098765L, CsvParserUtil.getLong(csvRecord, 2));
    }

    @Test
    @DisplayName("Получение значения типа Double из CSV-записи")
    void shouldGetDouble() throws IOException {
        // Arrange
        String csvData = "123.45,678.90,-42.0";
        CSVRecord csvRecord = parseRecord(csvData);

        // Act & Assert
        assertEquals(123.45, CsvParserUtil.getDouble(csvRecord, 0));
        assertEquals(678.90, CsvParserUtil.getDouble(csvRecord, 1));
        assertEquals(-42.0, CsvParserUtil.getDouble(csvRecord, 2));
        assertNull(CsvParserUtil.getDouble(csvRecord, 3));
    }

    @Test
    @DisplayName("Обработка невалидных значений типа Double")
    void shouldGetDoubleWithInvalidValues() throws IOException {
        // Arrange
        String csvData = "123.45,abc,678.90";
        CSVRecord csvRecord = parseRecord(csvData);

        // Act & Assert
        assertEquals(123.45, CsvParserUtil.getDouble(csvRecord, 0));
        assertNull(CsvParserUtil.getDouble(csvRecord, 1));
        assertEquals(678.90, CsvParserUtil.getDouble(csvRecord, 2));
    }

    /**
     * Создает CSVRecord из строки с разделителем запятая.
     */
    private CSVRecord parseRecord(String csvData) throws IOException {
        try (StringReader reader = new StringReader(csvData);
             CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {
            List<CSVRecord> records = parser.getRecords();
            return records.getFirst();
        }
    }
}