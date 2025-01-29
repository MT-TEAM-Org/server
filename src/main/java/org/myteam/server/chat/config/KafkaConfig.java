//package org.myteam.server.chat.config;
//
//
//import org.apache.kafka.clients.admin.AdminClientConfig;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.myteam.server.chat.domain.Chat;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.*;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@EnableKafka
//@Configuration
//public class KafkaConfig {
//
//    private static final String BOOTSTRAP_SERVERS = "kafka:9092";
//    private static final String DEFAULT_GROUP_ID = "chat-group";
//
//    @Bean
//    public KafkaAdmin kafkaAdmin() {
//        Map<String, Object> configs = new HashMap<>();
//        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        return new KafkaAdmin(configs);
//    }
//
//    /**
//     * Kafka ProducerFactory를 생성하는 Bean 메서드
//     */
//    @Bean
//    public ProducerFactory<String, Chat> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(producerConfigurations());
//    }
//
//    /**
//     * Kafka Producer 구성을 위한 설정값들을 포함한 맵을 반환하는 메서드
//     */
//    @Bean
//    public Map<String, Object> producerConfigurations() {
//        Map<String, Object> producerConfigurations = new HashMap<>();
//
//        producerConfigurations.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        producerConfigurations.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        producerConfigurations.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        producerConfigurations.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false); // JSON 타입 헤더 제거 (선택사항)
//
//        return producerConfigurations;
//    }
//
//    /**
//     * KafkaTemplate을 생성하는 Bean 메서드
//     */
//    @Bean
//    public KafkaTemplate<String, Chat> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
//
//    /**
//     * Kafka ConsumerFactory를 생성하는 Bean 메서드
//     */
//    @Bean
//    public ConsumerFactory<String, Chat> consumerFactory() {
//        JsonDeserializer<Chat> deserializer = new JsonDeserializer<>(Chat.class);
//        deserializer.addTrustedPackages("*"); // 모든 패키지 신뢰 (필요 시 제한적으로 변경)
//
//        Map<String, Object> consumerConfigurations = Map.of(
//                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS,
//                ConsumerConfig.GROUP_ID_CONFIG, DEFAULT_GROUP_ID,
//                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
//                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer,
//                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"
//        );
//
//        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), deserializer);
//    }
//
//    /**
//     * KafkaListener 컨테이너 팩토리를 생성하는 Bean 메서드
//     */
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, Chat> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, Chat> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//
//        factory.setConcurrency(3); // 병렬 처리 설정 (기본값 1)
//        factory.getContainerProperties().setPollTimeout(3000L); // 폴링 시간 설정 (선택사항)
//
//        return factory;
//    }
//}
