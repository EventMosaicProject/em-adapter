package com.neighbor.eventmosaic.adapter.publisher;

import com.neighbor.eventmosaic.library.common.dto.Event;
import com.neighbor.eventmosaic.library.common.dto.Mention;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Компонент для отправки сообщений в Kafka
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessagePublisher {

    private static final String BATCH_ID_HEADER = "ID-Batch";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.producer.adapter-event}")
    private String adapterEventTopic;

    @Value("${kafka.topic.producer.adapter-mention}")
    private String adapterMentionTopic;



    /**
     * Отправляет сообщения в Kafka для событий (Event)
     *
     * @param events список событий
     */
    public void publishEventMessages(List<Event> events, String batchId) {
        log.info("Отправка {} событий с идентификатором батча: {}", events.size(), batchId);

        events.forEach(event -> {
            var message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.KEY, event.getGlobalEventId().toString())
                    .setHeader(KafkaHeaders.TOPIC, adapterEventTopic)
                    .setHeader(BATCH_ID_HEADER, batchId)
                    .build();

            kafkaTemplate.send(message);
        });
    }

    /**
     * Отправляет сообщения в Kafka для упоминаний (Mention)
     *
     * @param mentions список упоминаний
     */
    public void publishMentionMessages(List<Mention> mentions, String batchId) {
        log.info("Отправка {} упоминаний с идентификатором батча: {}", mentions.size(), batchId);

        mentions.forEach(mention -> {
            var message = MessageBuilder
                    .withPayload(mention)
                    .setHeader(KafkaHeaders.KEY, mention.getGlobalEventId().toString())
                    .setHeader(KafkaHeaders.TOPIC, adapterMentionTopic)
                    .setHeader(BATCH_ID_HEADER, batchId)
                    .build();

            kafkaTemplate.send(message);
        });
    }
}
