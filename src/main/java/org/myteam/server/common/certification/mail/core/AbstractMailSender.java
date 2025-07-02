package org.myteam.server.common.certification.mail.core;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractMailSender implements MailStrategy {

    @Value("${SENDER_EMAIL}")
    protected String senderEmail;
    protected final JavaMailSender javaMailSender;
    protected final SpringTemplateEngine templateEngine;

    // 📌 구체적인 본문과 제목은 구현 클래스에서 정의
    protected abstract String getSubject();
    protected abstract String getBody(String email);

    // 메일을 보낸다
    @Override
    public CompletableFuture<Void> send(String email) {
        log.info("📨 Sending email to {}", email);

        try {
            String subject = getSubject();
            String body = getBody(email);
            MimeMessage message = createMail(email, subject, body);

            javaMailSender.send(message);
            log.info("✅ 이메일 전송 완료 - email: {}", email);

            return CompletableFuture.completedFuture(null); // 🔁 반드시 반환
        } catch (MailException e) {
            log.error("❌ 이메일 전송 실패 - email: {}, reason: {}", email, e.getMessage());

            return CompletableFuture.failedFuture(new PlayHiveException(ErrorCode.SEND_EMAIL_ERROR));
        }
    }

    // 공통 이메일 생성 메서드
    private MimeMessage createMail(String email, String subject, String body) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);
        } catch (MessagingException e) {
            log.error("이메일 생성 중 에러 발생: {}", e.getMessage());
            throw new PlayHiveException(ErrorCode.CREATE_EMAIL_ACCOUNT_ERROR);
        }
        return message;
    }
}
