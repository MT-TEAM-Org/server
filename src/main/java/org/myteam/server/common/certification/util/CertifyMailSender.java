package org.myteam.server.common.certification.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.common.certification.domain.CertificationCode;
import org.myteam.server.common.mail.MailSender;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static org.myteam.server.global.exception.ErrorCode.INTERNAL_SERVER_ERROR;


@Slf4j
@Component
@RequiredArgsConstructor
public class CertifyMailSender implements MailSender {

    @Value("${SENDER_EMAIL}")
    private String senderEmail;
    private final JavaMailSender javaMailSender;
    private static final int EXPIRATION_MINUTES = 5; // 유효 시간 (5분)
    private final Map<String, CertificationCode> codeStorage = new ConcurrentHashMap<>();

    // 메일을 보낸다
    @Override
    public void send(String email) {
        log.info("Sending email to {}", email);

        // 인증 코드 생성
        CertificationCode certificationCode = createNumber();
        codeStorage.put(email, certificationCode); // 이메일별로 인증 코드 저장

        // 이메일 생성 및 발송
        MimeMessage message = createMail(email, certificationCode.getCode());
        javaMailSender.send(message);
        log.info("certificationCode ExpirationTime : {}", certificationCode.getExpirationTime());
    }

    // 인증번호를 생성한다.
    private CertificationCode createNumber() {
        int code = (int) (Math.random() * 900000) + 100000; // 6자리 인증 코드
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        return new CertificationCode(String.valueOf(code), expirationTime);
    }

    // 메일 내용을 생성한다.
    private MimeMessage createMail(String email, String code) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("이메일 인증");

            // 이메일 본문
            String body = buildEmailContent(code, codeStorage.get(email).getExpirationTime());
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException e) {
            log.error("이메일 생성 중 에러가 발생하였습니다. 에러 메시지 : {}", e.getMessage());
            throw new PlayHiveException(INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return message;
    }

    // 인증번호가 유효한지 검사한다.
    public boolean isCodeValid(String email, String inputCode) {
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
