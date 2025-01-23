package org.myteam.server.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicManagementService {

    private final KafkaAdmin kafkaAdmin;

    /**
     * 동적 토픽 생성
     */
    public void createTopic(String topicName, int partitions, short replicationFactor) {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            NewTopic topic = new NewTopic(topicName, partitions, replicationFactor);
            adminClient.createTopics(Collections.singletonList(topic)).all().get();
            log.info("Topic '{}' created successfully with {} partitions and replication factor {}.",
                    topicName, partitions, replicationFactor);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to create topic '{}': {}", topicName, e.getMessage());
            Thread.currentThread().interrupt(); // 인터럽트 복원
        }
    }

    /**
     * 동적 토픽 삭제
     */
    public void deleteTopic(String topicName) {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            adminClient.deleteTopics(Collections.singletonList(topicName)).all().get();
            log.info("Topic '{}' deleted successfully.", topicName);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to delete topic '{}': {}", topicName, e.getMessage());
            Thread.currentThread().interrupt(); // 인터럽트 복원
        }
    }
}