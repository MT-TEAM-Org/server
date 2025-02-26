package org.myteam.server.common.certification.util;

import lombok.extern.slf4j.Slf4j;
import org.myteam.server.common.certification.domain.CertificationCode;
import org.myteam.server.common.mail.service.AbstractMailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
public class CertifyMailStrategy extends AbstractMailSender {
    private final CertifyStorage certifyStorage;

    public CertifyMailStrategy(JavaMailSender javaMailSender, CertifyStorage certifyStorage) {
        super(javaMailSender);
        this.certifyStorage = certifyStorage;
    }

    @Override
    protected String getSubject() {
        return "이메일 인증 코드";
    }

    @Override
    protected String getBody(String email) {
        CertificationCode certificationCode = certifyStorage.putCertificationCode(email);
        return buildEmailContent(certificationCode.getCode(), certificationCode.getExpirationTime());
    }

    // 인증번호가 유효한지 검사한다.
    @Override
    public boolean verify(String email, String inputCode) {
        log.debug("검증 중.......");
        CertificationCode storedCode = certifyStorage.getCertificationCode(email);
        log.debug("stored code {}", storedCode.getCode());
        log.debug("inputCode {}", inputCode);

        boolean isValid = Objects.equals(storedCode.getCode(), inputCode); // 코드가 일치하면 유효

        if (isValid) {
            try {
                certifyStorage.deleteCertificationCode(email);
            } catch (Exception e) {
                log.error("❌ 인증 코드 삭제 실패 - email: {}", email, e);
            }
            certifyStorage.putCertifiedEmail(email);
        }

        return isValid;
    }

    // 메일 내용을 작성한다.
    public String buildEmailContent(String code, LocalDateTime expirationTime) {
        String expirationTimeStr = expirationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 이메일 본문 생성
        String body = "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + code + "</h1>";
        body += "<h3>감사합니다.</h3>";
        body += "<p style=\"color: #555555; font-size: 12px; text-align: left; margin-top: 20px;\">";
        body += "인증 코드는 유효 기간은 5분이며 " + expirationTimeStr + "까지 유효합니다.";
        body += "</p>";
        return body;
    }
}
