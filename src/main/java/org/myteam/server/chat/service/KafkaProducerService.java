package org.myteam.server.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.Chat;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Chat> kafkaTemplate;

    /**
     * Kafka로 메시지 전송
     */
    public void sendMessage(String topic, Chat chat) {
        kafkaTemplate.send(topic, chat);
        log.info("Sent message: {} to topic: {}", chat, topic);
    }
}
