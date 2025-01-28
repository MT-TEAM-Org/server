package org.myteam.server.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.ChatRoom;
import org.myteam.server.chat.repository.ChatRoomRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatReadService {

    private final ChatRoomRepository roomRepository;

    /**
     * TODO: 필요가 없을 듯. 경기 일정 보내주는 것은 다르기때문에 현재는 테스트 때문에 남겨둠.
     * 모든 채팅방 조회
     */
    public List<ChatRoom> findAllRooms() {
        return roomRepository.findAll();
    }

    /**
     * ID로 채팅방 조회
     */
    public ChatRoom findRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.ROOM_NOT_FOUND));
    }
}
