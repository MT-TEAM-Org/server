package org.myteam.server.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.Chat;
import org.myteam.server.chat.dto.request.ChatMessage;
import org.myteam.server.chat.dto.request.RoomRequest;
import org.myteam.server.chat.dto.response.ChatRoomResponse;
import org.myteam.server.chat.service.ChatReadService;
import org.myteam.server.chat.service.ChatService;
import org.myteam.server.global.web.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatReadService chatReadService;
    private final ChatService chatService;

    /**
     * TODO: Kafka를 통해 메시지 전송
     * 원인: EC2 프리티어 램 메모리 이슈로 인한 인스턴스 정지로 Kafka 현재 사용 불가능
     */
    @MessageMapping("/{roomId}")
    @SendTo("/room/{roomId}")
    public ChatMessage chat(@DestinationVariable Long roomId, ChatMessage message) {
        log.info("Sending message to room {}: {}", roomId, message);

        Chat chat = chatService.createChat(roomId, message.getSender(), message.getSenderEmail(), message.getMessage());

        return ChatMessage.builder()
                .roomId(roomId)
                .sender(chat.getSender())
                .senderEmail(chat.getSenderEmail())
                .message(chat.getMessage())
                .build();
    }

    /**
     * TODO: 필요없지 않을까? 왜냐면 경기 일정은 데이터 내려주는게 다르다보니..
     * 채팅방 생성
     */
    @PostMapping("/room")
    public ResponseEntity<ResponseDto<ChatRoomResponse>> createChatRoom(@RequestBody RoomRequest requestDto) {
        log.info("createChatRoom: {}", requestDto.getRoomName());

        ChatRoomResponse newRoomResponse = chatService.createChatRoom(requestDto.getRoomName());

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "Successfully create room",
                newRoomResponse
        ));
    }

    /**
     * TODO: 필요없지 않을까? 삭제는 비즈니스 로직에만 필요. 이거는 테스트를 위해 남겨둠
     * 채팅방 삭제
     */
    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<ResponseDto<String>> deleteChatRoom(@PathVariable Long roomId) {
        log.info("deleteChatRoom: {}", roomId);

        String deleteChatRoomName = chatService.deleteChatRoom(roomId);

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "Successfully delete room",
                deleteChatRoomName
        ));
    }

    /**
     * TODO: 필요없지 않을까? 왜냐면 경기 일정은 데이터 내려주는게 다르다보니..
     * 모든 채팅방 조회
     */
    @GetMapping("/room")
    public ResponseEntity<ResponseDto<List<ChatRoomResponse>>> getChatRoom() {
        log.info("get Room");

        List<ChatRoomResponse> chatRooms = chatReadService.findAllRooms();

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "Successfully find rooms",
                chatRooms
        ));
    }
}
