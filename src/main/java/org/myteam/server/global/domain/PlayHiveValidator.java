package org.myteam.server.global.domain;

import lombok.extern.slf4j.Slf4j;
import org.myteam.server.member.dto.MemberRoleUpdateRequest;

@Slf4j
public class PlayHiveValidator {
    public static boolean validate(MemberRoleUpdateRequest request) {
        // 환경 변수 읽기
        String clientId = System.getenv(PlayHive.CLIENT_ID_KEY);
        String clientSecret = System.getenv(PlayHive.CLIENT_SECRET_KEY);

        if (clientId == null || clientSecret == null) {
            log.error("환경변수가 존재하지 않습니다.");
            return false;
        }

        return clientId.equals(request.getClientId()) && clientSecret.equals(request.getSecretKey());
    }
}