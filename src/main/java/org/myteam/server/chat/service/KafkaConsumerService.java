package org.myteam.server.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.myteam.server.chat.domain.Chat;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topicPattern = "room-.*", groupId = "chat-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeMessage(ConsumerRecord<String, Chat> record) {
        String topic = record.topic();
        Chat chat = record.value();

        log.info("Consumed message from topic {}: {}", topic, chat);

        // WebSocket으로 클라이언트에게 메시지 전달
        String roomId = topic.split("-")[1];
        messagingTemplate.convertAndSend("/topic/room/" + roomId, chat);

    }
}