package org.myteam.server.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.Chat;
import org.myteam.server.chat.domain.ChatRoom;
import org.myteam.server.chat.dto.request.ChatMessage;
import org.myteam.server.chat.repository.ChatRoomRepository;

import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.chat.domain.FilterData;
import org.myteam.server.chat.repository.FilterDataRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository roomRepository;
    private final FilterDataRepository filterDataRepository;
    private final BadWordFilter badWordFilter;
    private final KafkaProducerService kafkaProducerService;
    private final TopicManagementService topicManagementService;

    /**
     * 애플리케이션 시작 시 필터링 단어 로드
     */
    @EventListener(ApplicationReadyEvent.class)
    public void loadFilteredWords() {
        List<String> words = filterDataRepository.findAll()
                .stream()
                .map(FilterData::getWord)
                .collect(Collectors.toList());
        badWordFilter.loadFilteredWords(words);
    }

    // ==========================
    // 채팅방 관련 메서드
    // ==========================
    /**
     * 모든 채팅방 조회
     */
    public List<ChatRoom> findAllRoom() {
        return roomRepository.findAll();
    }

    /**
     * ID로 채팅방 조회
     */
    public ChatRoom findRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.ROOM_NOT_FOUND));
    }

    /**
     * 채팅방 생성
     */
    public ChatRoom createChatRoom(String roomName) {
        ChatRoom newRoom = ChatRoom.builder()
                .name(roomName)
                .build();

        roomRepository.save(newRoom); // DB에 저장
        log.info("Chat room '{}' created.", roomName);

        // Kafka 토픽 생성
        String topicName = "room-" + newRoom.getId(); // 토픽 이름은 "room-{roomId}" 형식으로 설정
        topicManagementService.createTopic(topicName, 3, (short) 1);
        log.info("Kafka topic '{}' created for chat room '{}'.", topicName, roomName);

        return newRoom;
    }

    /**
     * 채팅방 삭제
     */
    public String deleteChatRoom(Long roomId) {
        ChatRoom chatRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.ROOM_NOT_FOUND));

        // Kafka 토픽 삭제
        try {
            topicManagementService.deleteTopic("room-" + roomId);
            log.info("Deleted Kafka topic: room-{}", roomId);
        } catch (Exception e) {
            log.error("Failed to delete Kafka topic for room ID: {}. Error: {}", roomId, e.getMessage());
            throw new PlayHiveException(ErrorCode.KAFKA_TOPIC_DELETE_FAILED);
        }

        // 채팅방 삭제
        roomRepository.delete(chatRoom);
        log.info("Deleted chat room with ID: {}", roomId);

        return chatRoom.getName();
    }

    // ==========================
    // 채팅 관련 메서드
    // ==========================

    /**
     * 채팅 생성
     */
    public Chat createChat(Long roomId, String sender, String senderEmail, String message) {
        if (message.length() > 500) {
            log.warn("Message length exceeds 500 characters. Trimming the message.");
            message = message.substring(0, 500);
        }

        ChatRoom room = findRoomById(roomId);
        String filteredMessage = badWordFilter.filterMessage(message);
        Chat chat = Chat.createChat(room, sender, senderEmail, filteredMessage);

        // Kafka에 메시지 전송
        kafkaProducerService.sendMessage("room-" + roomId, chat);

        return chat;
    }

    public void validateAndTrimMessage(ChatMessage message) {
        if (message.getMessage() != null && message.getMessage().length() > 500) {
            log.warn("Message length exceeds 500 characters. Trimming the message.");
            message.setMessage(message.getMessage().substring(0, 500));
        }
    }

    // ==========================
    // 필터링 관련 메서드
    // ==========================
    public void addFilteredWord(String word) {
        filterDataRepository.save(new FilterData(word));
        badWordFilter.addFilteredWord(word);
    }

    public void removeFilteredWord(String word) {
        filterDataRepository.deleteByWord(word);
        badWordFilter.removeFilteredWord(word);
    }
}