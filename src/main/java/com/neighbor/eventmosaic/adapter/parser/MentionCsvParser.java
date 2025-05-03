package com.neighbor.eventmosaic.adapter.parser;

import com.neighbor.eventmosaic.library.common.dto.Mention;
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
 * Парсер CSV файлов с упоминаниями GDELT
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MentionCsvParser implements CsvParser<Mention> {

    /**
     * Индексы колонок в CSV-файле (начиная с 0)
     */
    private static final class ColumnIndex {
        static final int GLOBAL_EVENT_ID = 0;
        static final int EVENT_TIME_DATE = 1;
        static final int MENTION_TIME_DATE = 2;
        static final int MENTION_TYPE = 3;
        static final int MENTION_SOURCE_NAME = 4;
        static final int MENTION_IDENTIFIER = 5;
        static final int SENTENCE_ID = 6;
        static final int ACTOR1_CHAR_OFFSET = 7;
        static final int ACTOR2_CHAR_OFFSET = 8;
        static final int ACTION_CHAR_OFFSET = 9;
        static final int IN_RAW_TEXT = 10;
        static final int CONFIDENCE = 11;
        static final int MENTION_DOC_LEN = 12;
        static final int MENTION_DOC_TONE = 13;
        static final int MENTION_DOC_TRANSLATION_INFO = 14;
    }

    /**
     * Парсит CSV-файл упоминаний GDELT.
     * Запись с пустым GlobalEventId пропускается
     * так как это значение является ключом при отправке в Kafka и должно быть.
     *
     * @param stream  поток ввода
     * @param charset кодировка символов
     * @return список упоминаний
     * @throws IOException если возникает ошибка при чтении или парсинге CSV-файла
     */
    @Override
    public List<Mention> parseStream(InputStream stream, Charset charset) throws IOException {
        log.debug("Начинаем парсинг CSV-файла упоминаний");
        List<Mention> mentions = new ArrayList<>();

        CSVParser csvParser = CSVFormat.DEFAULT
                .builder()
                .setDelimiter('\t')         // Разделитель - табуляция
                .setSkipHeaderRecord(false) // Пропуск заголовка (если есть)
                .setTrim(true)              // Удаление пробелов в начале и конце строки
                .setIgnoreEmptyLines(true)  // Игнорирование пустых строк
                .get()
                .parse(new InputStreamReader(stream, charset));

        for (CSVRecord rec : csvParser) {
            Mention mention = parseRecord(rec);
            if (mention.getGlobalEventId() == null) {
                log.warn("Пропускаем запись с пустым GlobalEventId: {}", rec);
                continue;
            }
            mentions.add(mention);
        }

        log.info("Парсинг CSV-файла завершен. Обработано записей: {}", mentions.size());
        return mentions;
    }

    @Override
    public Class<Mention> getSupportedClass() {
        return Mention.class;
    }

    /**
     * Преобразует CSV-запись в объект Mention
     */
    private Mention parseRecord(CSVRecord csvRecord) {
        Mention mention = new Mention();

        mention.setGlobalEventId(getLong(csvRecord, ColumnIndex.GLOBAL_EVENT_ID));
        mention.setEventTimeDate(getLong(csvRecord, ColumnIndex.EVENT_TIME_DATE));
        mention.setMentionTimeDate(getLong(csvRecord, ColumnIndex.MENTION_TIME_DATE));
        mention.setMentionType(getInteger(csvRecord, ColumnIndex.MENTION_TYPE));
        mention.setMentionSourceName(getString(csvRecord, ColumnIndex.MENTION_SOURCE_NAME));
        mention.setMentionIdentifier(getString(csvRecord, ColumnIndex.MENTION_IDENTIFIER));
        mention.setSentenceId(getInteger(csvRecord, ColumnIndex.SENTENCE_ID));
        mention.setActor1CharOffset(getInteger(csvRecord, ColumnIndex.ACTOR1_CHAR_OFFSET));
        mention.setActor2CharOffset(getInteger(csvRecord, ColumnIndex.ACTOR2_CHAR_OFFSET));
        mention.setActionCharOffset(getInteger(csvRecord, ColumnIndex.ACTION_CHAR_OFFSET));
        mention.setInRawText(getInteger(csvRecord, ColumnIndex.IN_RAW_TEXT));
        mention.setConfidence(getInteger(csvRecord, ColumnIndex.CONFIDENCE));
        mention.setMentionDocLen(getInteger(csvRecord, ColumnIndex.MENTION_DOC_LEN));
        mention.setMentionDocTone(getDouble(csvRecord, ColumnIndex.MENTION_DOC_TONE));
        mention.setMentionDocTranslationInfo(getString(csvRecord, ColumnIndex.MENTION_DOC_TRANSLATION_INFO));

        return mention;
    }
}