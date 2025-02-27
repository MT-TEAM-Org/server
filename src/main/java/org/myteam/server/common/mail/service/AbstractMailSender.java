package org.myteam.server.common.mail.service;

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

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractMailSender implements MailStrategy {

    @Value("${SENDER_EMAIL}")
    protected String senderEmail;
    protected final JavaMailSender javaMailSender;

    // 📌 구체적인 본문과 제목은 구현 클래스에서 정의
    protected abstract String getSubject();
    protected abstract String getBody(String email);

    // 메일을 보낸다
    @Override
    public void send(String email) {
        log.info("Sending email to {}", email);

        try {
            // 📌 각 구현체에서 제공하는 데이터 생성 메서드
            String subject = getSubject();
            String body = getBody(email);

            // 📌 공통 이메일 생성
            MimeMessage message = createMail(email, subject, body);
            javaMailSender.send(message);

            log.info("이메일 전송 완료 - email: {}", email);
        } catch (MailException e) {
            log.error("이메일 전송 중 에러 발생: {}", e.getMessage());
            throw new PlayHiveException(ErrorCode.SEND_EMAIL_ERROR);
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
