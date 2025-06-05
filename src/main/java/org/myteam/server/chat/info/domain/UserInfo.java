package org.myteam.server.chat.info.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private UUID publicId;
    private String nickname;
    private String profileImage;
}