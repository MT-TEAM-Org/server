package org.myteam.server.common.certification.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CertificationCode {
    private static final int EXPIRATION_MINUTES = 5; // 유효 시간 (5분)
    private final String code;
    private final LocalDateTime expirationTime;

    // 인증번호를 생성한다.
    public static CertificationCode createNumber() {
        int code = (int) (Math.random() * 900000) + 100000; // 6자리 인증 코드
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        return new CertificationCode(String.valueOf(code), expirationTime);
    }
}
