package org.myteam.server.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myteam.server.member.entity.Member;

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

        public static ProfileResponse createProfileResponse(Member member) {
            return ProfileResponse.builder()
                    .memberPublicId(member.getPublicId())
                    .email(member.getEmail())
                    .phoneNumber(member.getTel())
                    .nickname(member.getNickname())
                    .build();
        }
    }
}
