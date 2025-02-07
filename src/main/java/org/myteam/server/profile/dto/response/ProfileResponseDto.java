package org.myteam.server.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public record ProfileResponseDto() {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileResponse {
        UUID memberPublicId;
        String email;
        String phoneNumber;
        String nickname;
    }
}
