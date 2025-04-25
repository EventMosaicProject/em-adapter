package com.neighbor.eventmosaic.adapter.parser;

import com.neighbor.eventmosaic.adapter.dto.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.neighbor.eventmosaic.adapter.util.CsvParserUtil.getDouble;
import static com.neighbor.eventmosaic.adapter.util.CsvParserUtil.getInteger;
import static com.neighbor.eventmosaic.adapter.util.CsvParserUtil.getLong;
import static com.neighbor.eventmosaic.adapter.util.CsvParserUtil.getString;

/**
 * Парсер CSV файлов с событиями GDELT
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventCsvParser implements CsvParser<Event> {

    /**
     * Индексы колонок в CSV-файле (начиная с 0)
     */
    private static final class ColumnIndex {
        static final int GLOBAL_EVENT_ID = 0;
        static final int DAY = 1;
        static final int MONTH_YEAR = 2;
        static final int YEAR = 3;
        static final int FRACTION_DATE = 4;
        static final int ACTOR1_CODE = 5;
        static final int ACTOR1_NAME = 6;
        static final int ACTOR1_COUNTRY_CODE = 7;
        static final int ACTOR1_KNOWN_GROUP_CODE = 8;
        static final int ACTOR1_ETHNIC_CODE = 9;
        static final int ACTOR1_RELIGION1_CODE = 10;
        static final int ACTOR1_RELIGION2_CODE = 11;
        static final int ACTOR1_TYPE1_CODE = 12;
        static final int ACTOR1_TYPE2_CODE = 13;
        static final int ACTOR1_TYPE3_CODE = 14;
        static final int ACTOR2_CODE = 15;
        static final int ACTOR2_NAME = 16;
        static final int ACTOR2_COUNTRY_CODE = 17;
        static final int ACTOR2_KNOWN_GROUP_CODE = 18;
        static final int ACTOR2_ETHNIC_CODE = 19;
        static final int ACTOR2_RELIGION1_CODE = 20;
        static final int ACTOR2_RELIGION2_CODE = 21;
        static final int ACTOR2_TYPE1_CODE = 22;
        static final int ACTOR2_TYPE2_CODE = 23;
        static final int ACTOR2_TYPE3_CODE = 24;
        static final int IS_ROOT_EVENT = 25;
        static final int EVENT_CODE = 26;
        static final int EVENT_BASE_CODE = 27;
        static final int EVENT_ROOT_CODE = 28;
        static final int QUAD_CLASS = 29;
        static final int GOLDSTEIN_SCALE = 30;
        static final int NUM_MENTIONS = 31;
        static final int NUM_SOURCES = 32;
        static final int NUM_ARTICLES = 33;
        static final int AVG_TONE = 34;
        static final int ACTOR1_GEO_TYPE = 35;
        static final int ACTOR1_GEO_FULLNAME = 36;
        static final int ACTOR1_GEO_COUNTRY_CODE = 37;
        static final int ACTOR1_GEO_ADM1_CODE = 38;
        static final int ACTOR1_GEO_ADM2_CODE = 39;
        static final int ACTOR1_GEO_LAT = 40;
        static final int ACTOR1_GEO_LONG = 41;
        static final int ACTOR1_GEO_FEATURE_ID = 42;
        static final int ACTOR2_GEO_TYPE = 43;
        static final int ACTOR2_GEO_FULLNAME = 44;
        static final int ACTOR2_GEO_COUNTRY_CODE = 45;
        static final int ACTOR2_GEO_ADM1_CODE = 46;
        static final int ACTOR2_GEO_ADM2_CODE = 47;
        static final int ACTOR2_GEO_LAT = 48;
        static final int ACTOR2_GEO_LONG = 49;
        static final int ACTOR2_GEO_FEATURE_ID = 50;
        static final int ACTION_GEO_TYPE = 51;
        static final int ACTION_GEO_FULLNAME = 52;
        static final int ACTION_GEO_COUNTRY_CODE = 53;
        static final int ACTION_GEO_ADM1_CODE = 54;
        static final int ACTION_GEO_ADM2_CODE = 55;
        static final int ACTION_GEO_LAT = 56;
        static final int ACTION_GEO_LONG = 57;
        static final int ACTION_GEO_FEATURE_ID = 58;
        static final int DATE_ADDED = 59;
        static final int SOURCE_URL = 60;
    }

    /**
     * Парсит CSV-файл событий GDELT.
     * Запись с пустым GlobalEventId пропускается
     * так как это значение является ключом при отправке в Kafka и должно быть.
     *
     * @param stream  поток ввода
     * @param charset кодировка символов
     * @return список событий
     * @throws IOException если возникает ошибка при чтении или парсинге CSV-файла
     */
    @Override
    public List<Event> parseStream(InputStream stream, Charset charset) throws IOException {
        log.debug("Начинаем парсинг CSV-файла событий");
        List<Event> events = new ArrayList<>();

        CSVParser csvParser = CSVFormat.DEFAULT
                .builder()
                .setDelimiter('\t')         // Разделитель - табуляция
                .setSkipHeaderRecord(false) // Пропуск заголовка
                .setTrim(true)              // Удаление пробелов в начале и конце строки
                .setIgnoreEmptyLines(true)  // Игнорирование пустых строк
                .get()
                .parse(new InputStreamReader(stream, charset));

        for (CSVRecord rec : csvParser) {
            Event event = parseRecord(rec);
            if (event.getGlobalEventId() == null) {
                log.warn("Пропускаем запись с пустым GlobalEventId: {}", rec);
                continue;
            }
            events.add(event);
        }

        log.info("Парсинг CSV-файла завершен. Обработано записей: {}", events.size());
        return events;
    }

    @Override
    public Class<Event> getSupportedClass() {
        return Event.class;
    }

    /**
     * Преобразует CSV-запись в объект Event
     */
    private Event parseRecord(CSVRecord csvRecord) {
        Event event = new Event();

        event.setGlobalEventId(getLong(csvRecord, ColumnIndex.GLOBAL_EVENT_ID));
        event.setDay(getInteger(csvRecord, ColumnIndex.DAY));
        event.setMonthYear(getInteger(csvRecord, ColumnIndex.MONTH_YEAR));
        event.setYear(getInteger(csvRecord, ColumnIndex.YEAR));
        event.setFractionDate(getDouble(csvRecord, ColumnIndex.FRACTION_DATE));
        event.setActor1Code(getString(csvRecord, ColumnIndex.ACTOR1_CODE));
        event.setActor1Name(getString(csvRecord, ColumnIndex.ACTOR1_NAME));
        event.setActor1CountryCode(getString(csvRecord, ColumnIndex.ACTOR1_COUNTRY_CODE));
        event.setActor1KnownGroupCode(getString(csvRecord, ColumnIndex.ACTOR1_KNOWN_GROUP_CODE));
        event.setActor1EthnicCode(getString(csvRecord, ColumnIndex.ACTOR1_ETHNIC_CODE));
        event.setActor1Religion1Code(getString(csvRecord, ColumnIndex.ACTOR1_RELIGION1_CODE));
        event.setActor1Religion2Code(getString(csvRecord, ColumnIndex.ACTOR1_RELIGION2_CODE));
        event.setActor1Type1Code(getString(csvRecord, ColumnIndex.ACTOR1_TYPE1_CODE));
        event.setActor1Type2Code(getString(csvRecord, ColumnIndex.ACTOR1_TYPE2_CODE));
        event.setActor1Type3Code(getString(csvRecord, ColumnIndex.ACTOR1_TYPE3_CODE));
        event.setActor2Code(getString(csvRecord, ColumnIndex.ACTOR2_CODE));
        event.setActor2Name(getString(csvRecord, ColumnIndex.ACTOR2_NAME));
        event.setActor2CountryCode(getString(csvRecord, ColumnIndex.ACTOR2_COUNTRY_CODE));
        event.setActor2KnownGroupCode(getString(csvRecord, ColumnIndex.ACTOR2_KNOWN_GROUP_CODE));
        event.setActor2EthnicCode(getString(csvRecord, ColumnIndex.ACTOR2_ETHNIC_CODE));
        event.setActor2Religion1Code(getString(csvRecord, ColumnIndex.ACTOR2_RELIGION1_CODE));
        event.setActor2Religion2Code(getString(csvRecord, ColumnIndex.ACTOR2_RELIGION2_CODE));
        event.setActor2Type1Code(getString(csvRecord, ColumnIndex.ACTOR2_TYPE1_CODE));
        event.setActor2Type2Code(getString(csvRecord, ColumnIndex.ACTOR2_TYPE2_CODE));
        event.setActor2Type3Code(getString(csvRecord, ColumnIndex.ACTOR2_TYPE3_CODE));
        event.setIsRootEvent(getInteger(csvRecord, ColumnIndex.IS_ROOT_EVENT));
        event.setEventCode(getString(csvRecord, ColumnIndex.EVENT_CODE));
        event.setEventBaseCode(getString(csvRecord, ColumnIndex.EVENT_BASE_CODE));
        event.setEventRootCode(getString(csvRecord, ColumnIndex.EVENT_ROOT_CODE));
        event.setQuadClass(getInteger(csvRecord, ColumnIndex.QUAD_CLASS));
        event.setGoldsteinScale(getDouble(csvRecord, ColumnIndex.GOLDSTEIN_SCALE));
        event.setNumMentions(getInteger(csvRecord, ColumnIndex.NUM_MENTIONS));
        event.setNumSources(getInteger(csvRecord, ColumnIndex.NUM_SOURCES));
        event.setNumArticles(getInteger(csvRecord, ColumnIndex.NUM_ARTICLES));
        event.setAvgTone(getDouble(csvRecord, ColumnIndex.AVG_TONE));
        event.setActor1GeoType(getInteger(csvRecord, ColumnIndex.ACTOR1_GEO_TYPE));
        event.setActor1GeoFullName(getString(csvRecord, ColumnIndex.ACTOR1_GEO_FULLNAME));
        event.setActor1GeoCountryCode(getString(csvRecord, ColumnIndex.ACTOR1_GEO_COUNTRY_CODE));
        event.setActor1GeoAdm1Code(getString(csvRecord, ColumnIndex.ACTOR1_GEO_ADM1_CODE));
        event.setActor1GeoAdm2Code(getString(csvRecord, ColumnIndex.ACTOR1_GEO_ADM2_CODE));
        event.setActor1GeoLat(getDouble(csvRecord, ColumnIndex.ACTOR1_GEO_LAT));
        event.setActor1GeoLong(getDouble(csvRecord, ColumnIndex.ACTOR1_GEO_LONG));
        event.setActor1GeoFeatureId(getString(csvRecord, ColumnIndex.ACTOR1_GEO_FEATURE_ID));
        event.setActor2GeoType(getInteger(csvRecord, ColumnIndex.ACTOR2_GEO_TYPE));
        event.setActor2GeoFullName(getString(csvRecord, ColumnIndex.ACTOR2_GEO_FULLNAME));
        event.setActor2GeoCountryCode(getString(csvRecord, ColumnIndex.ACTOR2_GEO_COUNTRY_CODE));
        event.setActor2GeoAdm1Code(getString(csvRecord, ColumnIndex.ACTOR2_GEO_ADM1_CODE));
        event.setActor2GeoAdm2Code(getString(csvRecord, ColumnIndex.ACTOR2_GEO_ADM2_CODE));
        event.setActor2GeoLat(getDouble(csvRecord, ColumnIndex.ACTOR2_GEO_LAT));
        event.setActor2GeoLong(getDouble(csvRecord, ColumnIndex.ACTOR2_GEO_LONG));
        event.setActor2GeoFeatureId(getString(csvRecord, ColumnIndex.ACTOR2_GEO_FEATURE_ID));
        event.setActionGeoType(getInteger(csvRecord, ColumnIndex.ACTION_GEO_TYPE));
        event.setActionGeoFullName(getString(csvRecord, ColumnIndex.ACTION_GEO_FULLNAME));
        event.setActionGeoCountryCode(getString(csvRecord, ColumnIndex.ACTION_GEO_COUNTRY_CODE));
        event.setActionGeoAdm1Code(getString(csvRecord, ColumnIndex.ACTION_GEO_ADM1_CODE));
        event.setActionGeoAdm2Code(getString(csvRecord, ColumnIndex.ACTION_GEO_ADM2_CODE));
        event.setActionGeoLat(getDouble(csvRecord, ColumnIndex.ACTION_GEO_LAT));
        event.setActionGeoLong(getDouble(csvRecord, ColumnIndex.ACTION_GEO_LONG));
        event.setActionGeoFeatureId(getString(csvRecord, ColumnIndex.ACTION_GEO_FEATURE_ID));
        event.setDateAdded(getLong(csvRecord, ColumnIndex.DATE_ADDED));
        event.setSourceUrl(getString(csvRecord, ColumnIndex.SOURCE_URL));

        return event;
    }
}