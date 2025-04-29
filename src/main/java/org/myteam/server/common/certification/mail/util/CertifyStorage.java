package org.myteam.server.common.certification.mail.util;


import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.myteam.server.common.certification.domain.CertificationCode;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CertifyStorage {
    private static final int EXPIRATION_MINUTES = 5; // 유효 시간 (5분)

    private final Map<String, CertificationCode> codeStorage = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> certifiedStorage = new ConcurrentHashMap<>();

    /**
     * 인증 코드 저장
     */
    public CertificationCode putCertificationCode(String email) {
        CertificationCode certificationCode = CertificationCode.createNumber();
        codeStorage.put(email, certificationCode);
        log.info("✅ 인증 코드 저장 - email: {}, code: {}", email, certificationCode.getCode());

        return certificationCode;
    }

    /**
     * 인증 코드 조회
     */
    public CertificationCode getCertificationCode(String email) {
        CertificationCode certificationCode = codeStorage.get(email);
        if (certificationCode == null || LocalDateTime.now().isAfter(certificationCode.getExpirationTime())) {
            log.warn("⚠️ 인증 코드 없거나 만료되었음. - email: {}", email);
            throw new PlayHiveException(ErrorCode.NOT_FOUND_CERTIFICATION_CODE);
        }
        log.debug("stored code {}", certificationCode.getCode());
        return certificationCode;
    }

    /**
     * 인증코드 삭제
     */
    public void deleteCertificationCode(String email) {
        if (email == null) {
            log.warn("⚠️ 삭제 요청된 이메일이 null입니다.");
            throw new PlayHiveException(ErrorCode.INVALID_EMAIL);
        }

        if (codeStorage.containsKey(email)) {
            codeStorage.remove(email);
            log.info("✅ 인증코드 삭제 완료 - email: {}", email);
        } else {
            log.warn("⚠️ 삭제하려는 인증 코드가 존재하지 않음 - email: {}", email);
        }
    }

    /**
     * 인증 성공한 사용자 저장
     */
    public void putCertifiedEmail(String email) {
        LocalDateTime now = createExpiredTime();
        certifiedStorage.put(email, now);
        log.info("✅ 인증 성공 저장 - email: {}, 인증 시간: {}", email, now);
    }

    /**
     * 인증된 이메일 조회
     */
    public boolean isCertified(String email) {
        boolean exists = certifiedStorage.containsKey(email);
        if (!exists) {
            log.warn("⚠️ 인증되지 않은 이메일 - email: {}", email);
        }
        return exists;
    }

    private LocalDateTime createExpiredTime() {
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        return expirationTime;
    }

    /**
     * 만료된 인증 코드 & 인증된 사용자 제거
     */
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void clearExpiredCodes() {
        log.info("🗑️ 만료된 인증 코드 및 인증 정보 제거 시작");

        LocalDateTime now = LocalDateTime.now();
        codeStorage.entrySet().removeIf(entry -> now.isAfter(entry.getValue().getExpirationTime()));
        certifiedStorage.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));

        log.debug("만료된 코드 제거 로직 END");
    }
}

