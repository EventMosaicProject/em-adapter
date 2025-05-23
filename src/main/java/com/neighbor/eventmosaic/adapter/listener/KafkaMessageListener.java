package com.neighbor.eventmosaic.adapter.listener;

import com.neighbor.eventmosaic.adapter.publisher.KafkaMessagePublisher;
import com.neighbor.eventmosaic.adapter.service.CsvProcessingService;
import com.neighbor.eventmosaic.adapter.util.FileNameUtil;
import com.neighbor.eventmosaic.library.common.dto.Event;
import com.neighbor.eventmosaic.library.common.dto.Mention;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Компонент для прослушивания сообщений из Kafka
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageListener {

    private final CsvProcessingService csvProcessingService;
    private final KafkaMessagePublisher kafkaMessagePublisher;

    /**
     * Обрабатывает сообщения с путями к файлам событий (Event)
     *
     * @param path путь к CSV файлу с событиями
     */
    @KafkaListener(topics = "${kafka.topic.consumer.collector-event}")
    public void processEventsPath(String path) {
        log.info("Получен путь к файлу событий: {}", path);
        String batchId = FileNameUtil.extractBatchId(path);

        List<Event> events = csvProcessingService.processCsvFile(path, Event.class);
        kafkaMessagePublisher.publishEventMessages(events, batchId);
    }

    /**
     * Обрабатывает сообщения с путями к файлам упоминаний (Mention)
     *
     * @param path путь к CSV файлу с упоминаниями
     */
    @KafkaListener(topics = "${kafka.topic.consumer.collector-mention}")
    public void processMentionsPath(String path) {
        log.info("Получен путь к файлу упоминаний: {}", path);
        String batchId = FileNameUtil.extractBatchId(path);

        List<Mention> mentions = csvProcessingService.processCsvFile(path, Mention.class);
        kafkaMessagePublisher.publishMentionMessages(mentions, batchId);
    }
}
