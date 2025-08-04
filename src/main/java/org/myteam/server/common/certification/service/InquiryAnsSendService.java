package org.myteam.server.common.certification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.admin.entity.AdminContentMemo;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.member.controller.response.MemberResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class InquiryAnsSendService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final InquiryRepository inquiryRepository;
    @Value("${SENDER_EMAIL}")
    private String senderEmail;

    private String getSubject() {
        return "문의 답변 메일입니다.";
    }
    private String getBody(AdminContentMemo adminContentMemo,MemberResponse memberResponse) {
        Long inquiryId=adminContentMemo.getContentId();
        Inquiry inquiry=inquiryRepository.findById(inquiryId).get();
        Context context = new Context();
        context.setVariable("content",inquiry.getContent());
        context.setVariable("answer",adminContentMemo.getContent());
        context.setVariable("inquiryMeta.meta.ip",inquiry.getClientIp());
        context.setVariable(" inquiryMeta.meta.created_at",
                DateFormatUtil.formatByDot.format(inquiry.getCreatedAt()));
        return templateEngine.process("mail/admin-to-user-reply", context);
    }
    @Async
    public CompletableFuture<Void> send(AdminContentMemo adminMemo, MemberResponse memberResponse) {

        log.info("📨 Sending email to {}",memberResponse.getEmail());

        try {
            String subject = getSubject();
            String body = getBody(adminMemo,memberResponse);
            MimeMessage message = createMail(memberResponse.getEmail(), subject, body);

            javaMailSender.send(message);
            log.info("✅ 문의 답변 이메일 전송 완료 - email: {}", memberResponse.getEmail());

            return CompletableFuture.completedFuture(null); // 🔁 반드시 반환
        } catch (MailException e) {
            log.error("❌ 문의 답변 이메일 전송 실패 - email: {}, reason: {}", memberResponse.getEmail(), e.getMessage());

            return CompletableFuture.failedFuture(new PlayHiveException(ErrorCode.SEND_EMAIL_ERROR));
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

}
