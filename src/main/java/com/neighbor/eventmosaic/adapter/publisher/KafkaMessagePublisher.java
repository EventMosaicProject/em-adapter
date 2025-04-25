package com.neighbor.eventmosaic.adapter.publisher;

import com.neighbor.eventmosaic.adapter.dto.Event;
import com.neighbor.eventmosaic.adapter.dto.Mention;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Компонент для отправки сообщений в Kafka
 */
@Component
@RequiredArgsConstructor
public class KafkaMessagePublisher {

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
    public void publishEventMessages(List<Event> events) {
        events.forEach(event ->
                kafkaTemplate.send(adapterEventTopic, event.getGlobalEventId().toString(), event));
    }

    /**
     * Отправляет сообщения в Kafka для упоминаний (Mention)
     *
     * @param mentions список упоминаний
     */
    public void publishMentionMessages(List<Mention> mentions) {
        mentions.forEach(mention ->
                kafkaTemplate.send(adapterMentionTopic, mention.getGlobalEventId().toString(), mention));
    }
}
