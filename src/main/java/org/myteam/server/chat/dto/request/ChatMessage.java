package org.myteam.server.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    private String sender;
    private Long roomId;
    private String senderEmail;
    private String message;
    private LocalDateTime sendDate;

}