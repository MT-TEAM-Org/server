package org.myteam.server.common.certification.mail.strategy;


import lombok.extern.slf4j.Slf4j;
import org.myteam.server.common.certification.mail.core.AbstractMailSender;
import org.myteam.server.common.certification.mail.domain.EmailType;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class NotifyAdminSuspendGlobalStrategy extends AbstractMailSender {



    @Value("${SENDER_EMAIL}")
    protected String senderEmail;

    private final MemberReadService memberReadService;

    public NotifyAdminSuspendGlobalStrategy(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine
            ,MemberReadService memberReadService) {
        super(javaMailSender, templateEngine);
        this.memberReadService=memberReadService;
    }

    @Override
    protected String getSubject() {
        return "관리자 정지 전체 전송메일입니다.";
    }

    @Override
    protected String getBody(String email) {
        return buildSuspendBodyContent(email);
    }

    public String buildSuspendBodyContent(String emailIp) {

        String [] emailIpArr=emailIp.split(">");

        Member member=memberReadService.findByEmail(emailIpArr[0]);
        Context context=new Context();
        context.setVariable("nickname",member.getNickname());
        context.setVariable("email",member.getEmail());
        context.setVariable("lock_date",
                DateFormatUtil.formatByDotAndSlash.format(LocalDateTime.now()));
        context.setVariable("ip","("+emailIpArr[1]+")");
        return templateEngine.process("mail/admin-alert-to-public", context);
    }




    @Override
    public EmailType getType() {
        return EmailType.NOTIFY_ADMIN_SUSPEND_GLOBAL;
    }

    @Async
    @Override
    public CompletableFuture<Void> send(String email) {
        return super.send(senderEmail);
    }
}
