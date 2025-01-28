package org.myteam.server.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.Chat;
import org.myteam.server.chat.domain.ChatRoom;
import org.myteam.server.chat.dto.request.ChatMessage;
import org.myteam.server.chat.dto.request.RoomRequest;
import org.myteam.server.chat.service.ChatReadService;
import org.myteam.server.chat.dto.request.FilterDataRequest;
import org.myteam.server.chat.service.ChatWriteService;
import org.myteam.server.chat.service.FilterWriteService;
import org.myteam.server.global.web.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatReadService chatReadService;
    private final ChatWriteService chatWriteService;
    private final FilterWriteService filterWriteService;

    /**
     * Kafka를 통해 메시지 전송
     */
    @PostMapping("/send/{roomId}")
    public ResponseEntity<ResponseDto<String>> sendMessageToRoom(@PathVariable Long roomId,
                                               @RequestBody ChatMessage message) {
        log.info("Sending message to room {}: {}", roomId, message);

        Chat chat = chatWriteService.createChat(roomId, message.getSender(), message.getSenderEmail(), message.getMessage());

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "Message sent to Kafka topic: room-" + roomId,
                null));
    }

    /**
     * TODO: 필요없지 않을까? 왜냐면 경기 일정은 데이터 내려주는게 다르다보니..
     * 채팅방 생성
     */
    @PostMapping("/room")
    public ResponseEntity<ResponseDto<ChatRoom>> createChatRoom(@RequestBody RoomRequest requestDto) {
        log.info("createChatRoom: {}", requestDto.getRoomName());

        ChatRoom newRoom = chatWriteService.createChatRoom(requestDto.getRoomName());

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "Successfully create room",
                newRoom
        ));
    }

    /**
     * TODO: 필요없지 않을까? 삭제는 비즈니스 로직에만 필요. 이거는 테스트를 위해 남겨둠
     * 채팅방 삭제
     */
    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<ResponseDto<String>> deleteChatRoom(@PathVariable Long roomId) {
        log.info("deleteChatRoom: {}", roomId);

        String deleteChatRoomName = chatWriteService.deleteChatRoom(roomId);

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
    public ResponseEntity<ResponseDto<List<ChatRoom>>> getChatRoom() {
        log.info("get Room");

        List<ChatRoom> chatRooms = chatReadService.findAllRooms();

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "Successfully find rooms",
                chatRooms
        ));
    }

    /**
     * 필터 데이터 추가
     */
    @PostMapping("/filter")
    public ResponseEntity<ResponseDto<String>> addFilterData(@RequestBody FilterDataRequest filterData) {
        log.info("addFilterData: {}", filterData);

        filterWriteService.addFilteredWord(filterData.getFilterData());

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "Successfully Filter data added",
                filterData.getFilterData()
        ));
    }

    /**
     * 필터 데이터 삭제
     */
    @DeleteMapping("/filter")
    public ResponseEntity<ResponseDto<String>> deleteFilterData(@RequestBody FilterDataRequest filterData) {
        log.info("deleteFilterData: {}", filterData);

        filterWriteService.removeFilteredWord(filterData.getFilterData());

        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "Successfully Filter data deleted",
                filterData.getFilterData()
        ));
    }
}
