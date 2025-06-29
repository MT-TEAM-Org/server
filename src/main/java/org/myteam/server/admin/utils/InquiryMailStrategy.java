package org.myteam.server.admin.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.admin.dto.AdminTask;
import org.myteam.server.common.certification.mail.core.AbstractMailSender;
import org.myteam.server.common.certification.mail.core.MailStrategy;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.myteam.server.admin.dto.AdminTask.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class InquiryMailStrategy{

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${SENDER_EMAIL}")
    protected String senderEmail;



    private String getSubject() {
        return "문의 내역 답변입니다";
    }

    private String getBody(String email,String inquiryContent,String response){
        return buildEmailContent(email,inquiryContent,response);

    }
    public void send(String email,String inquiryContent,String response){
        log.info("Sending email to {}", email);

        try {
            // 📌 각 구현체에서 제공하는 데이터 생성 메서드
            String subject = getSubject();
            String body = getBody(email,inquiryContent,response);

            // 📌 공통 이메일 생성
            MimeMessage message = createMail(email, subject, body);
            javaMailSender.send(message);

            log.info("이메일 전송 완료 - email: {}", email);
        } catch (MailException e) {
            log.error("이메일 전송 중 에러 발생: {}", e.getMessage());
            throw new PlayHiveException(ErrorCode.SEND_EMAIL_ERROR);
        }

    }

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

    private String buildEmailContent(String email,String inquiryContent, String response) {
        Context context = new Context();
        context.setVariable("email", email);
        context.setVariable("inquiryContent",inquiryContent);
        context.setVariable("response",response);
        return templateEngine.process("mail/inquiry-response-template", context);
    }

}
