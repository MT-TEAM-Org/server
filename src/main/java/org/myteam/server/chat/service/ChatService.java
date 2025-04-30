package org.myteam.server.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.chat.domain.Chat;
import org.myteam.server.chat.domain.ChatRoom;
import org.myteam.server.chat.dto.response.ChatRoomResponse;
import org.myteam.server.chat.mapper.ChatRoomMapper;
import org.myteam.server.chat.repository.ChatRoomRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRoomRepository roomRepository;
//    private final KafkaProducerService kafkaProducerService;
//    private final TopicManagementService topicManagementService;
    private final BadWordFilter badWordFilter;

    /**
     * 채팅방 생성
     */
    public ChatRoomResponse createChatRoom(String roomName) {
        ChatRoom newRoom = ChatRoom.builder()
                .name(roomName)
                .build();

        roomRepository.save(newRoom);
        log.info("Chat room '{}' created.", roomName);

        // Kafka 토픽 생성
        String topicName = "room-" + newRoom.getId();
//        topicManagementService.createTopic(topicName, 3, (short) 1);
        log.info("Kafka topic '{}' created for chat room '{}'.", topicName, roomName);

        return ChatRoomMapper.toDto(newRoom);
    }

    /**
     * 채팅방 삭제
     */
    public String deleteChatRoom(Long roomId) {
        ChatRoom chatRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.ROOM_NOT_FOUND));

        // Kafka 토픽 삭제
//        try {
//            topicManagementService.deleteTopic("room-" + roomId);
//            log.info("Deleted Kafka topic: room-{}", roomId);
//        } catch (Exception e) {
//            log.error("Failed to delete Kafka topic for room ID: {}. Error: {}", roomId, e.getMessage());
//            throw new PlayHiveException(ErrorCode.KAFKA_TOPIC_DELETE_FAILED);
//        }

        // 채팅방 삭제
        roomRepository.delete(chatRoom);
        log.info("Deleted chat room with ID: {}", roomId);

        return chatRoom.getName();
    }

    /**
     * 채팅 생성
     */
    public Chat createChat(Long roomId, String sender, String senderEmail, String message) {
        message = validateAndTrimMessage(message);

        ChatRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.ROOM_NOT_FOUND));

        String filteredMessage = badWordFilter.filterMessage(message);
        Chat chat = Chat.createChat(room, sender, senderEmail, filteredMessage);

        // Kafka에 메시지 전송
//        kafkaProducerService.sendMessage("room-" + roomId, chat);

        return chat;
    }

    public String validateAndTrimMessage(String message) {
        if (message != null && message.length() > 500) {
            log.warn("Message length exceeds 500 characters. Trimming the message.");
            return message.substring(0, 500);
        }
        return message;
    }
}
