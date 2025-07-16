package org.myteam.server.common.certification.mail.strategy;


import lombok.extern.slf4j.Slf4j;
import org.myteam.server.common.certification.mail.core.AbstractMailSender;
import org.myteam.server.common.certification.mail.domain.EmailType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.MemberReadService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class NotifyAdminSuspendStrategy extends AbstractMailSender {

    private final MemberReadService memberReadService;

    public NotifyAdminSuspendStrategy(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine,
                                      MemberReadService memberReadService) {
        super(javaMailSender, templateEngine);
        this.memberReadService=memberReadService;

    }

    @Override
    protected String getSubject() {
        return "해당 계정은 잠금 처리 되었습니다.";
    }

    @Override
    protected String getBody(String email) {
        return buildSuspendBodyContent(email);
    }

    public String buildSuspendBodyContent(String email) {

        Member member=memberReadService.findByEmail(email);
        // 이메일 본문 생성
        //전달받은 템플릿이 없으니 그냥 냅두기
        Context context=new Context();
        context.setVariable("nickname",member.getNickname());
        return templateEngine.process("mail/admin-account-lock", context);
    }

    @Override
    public EmailType getType() {
        return EmailType.NOTIFY_ADMIN_SUSPEND;
    }

    @Override
    @Async
    public CompletableFuture<Void> send(String email){
        return super.send(email);
    }
}
