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
    private static final int EXPIRATION_MINUTES = 5; // 유효 시간 (5분)
    private final Map<String, CertificationCode> codeStorage = new ConcurrentHashMap<>();

    public CertifyMailStrategy(JavaMailSender javaMailSender) {
        super(javaMailSender);
    }

    @Override
    protected String getSubject() {
        return "이메일 인증 코드";
    }

    @Override
    protected String getBody(String email) {
        CertificationCode certificationCode = createNumber();
        codeStorage.put(email, certificationCode);
        return buildEmailContent(certificationCode.getCode(), certificationCode.getExpirationTime());
    }

    // 인증번호를 생성한다.
    private CertificationCode createNumber() {
        int code = (int) (Math.random() * 900000) + 100000; // 6자리 인증 코드
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        return new CertificationCode(String.valueOf(code), expirationTime);
    }

    // 인증번호가 유효한지 검사한다.
    @Override
    public boolean verify(String email, String inputCode) {
        CertificationCode storedCode = codeStorage.get(email);

        if (storedCode == null || LocalDateTime.now().isAfter(storedCode.getExpirationTime())) {
            return false; // 코드가 없거나 만료된 경우
        }

        log.debug("검증 중.......");
        log.debug("stored code {}", storedCode.getCode());
        log.debug("inputCode {}", inputCode);

        return Objects.equals(storedCode.getCode(), inputCode); // 코드가 일치하면 유효
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

    // 만료된 코드 제거
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void clearExpiredCodes() {
        log.debug("만료된 코드 제거 로직 START");

        LocalDateTime now = LocalDateTime.now();
        codeStorage.entrySet().removeIf(entry -> now.isAfter(entry.getValue().getExpirationTime()));

        log.debug("만료된 코드 제거 로직 END");
    }

    // codeStorage 내용 출력
    public void print() {
        log.debug("codeStorage 내용 출력");
        log.debug(codeStorage.toString());
    }
}
