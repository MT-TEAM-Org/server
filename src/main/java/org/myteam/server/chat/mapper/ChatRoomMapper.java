package org.myteam.server.chat.mapper;

import org.myteam.server.chat.domain.ChatRoom;
import org.myteam.server.chat.dto.response.ChatRoomResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ChatRoomMapper {
    public static ChatRoomResponse toDto(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .build();
    }

    public static List<ChatRoomResponse> toDtoList(List<ChatRoom> chatRooms) {
        return chatRooms.stream()
                .map(ChatRoomMapper::toDto)
                .collect(Collectors.toList());
    }
}
