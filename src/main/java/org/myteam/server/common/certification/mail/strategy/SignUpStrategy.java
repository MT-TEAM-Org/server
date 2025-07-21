package org.myteam.server.common.certification.mail.strategy;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.common.certification.mail.core.AbstractMailSender;
import org.myteam.server.common.certification.mail.domain.EmailType;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Component
public class SignUpStrategy extends AbstractMailSender {

    private final MemberJpaRepository memberJpaRepository;

    public SignUpStrategy(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine,
                          MemberJpaRepository memberJpaRepository) {
        super(javaMailSender, templateEngine);
        this.memberJpaRepository = memberJpaRepository;
    }

    @Override
    protected String getSubject() {
        return "PlayHive 회원가입완료";
    }

    @Override
    protected String getBody(String email) {
        return buildWelcomeContent(email);
    }

    private String buildWelcomeContent(String email) {
        String nickname = memberJpaRepository.findByNicknameAndStatus(email, MemberStatus.ACTIVE)
                .map(Member::getNickname)
                .orElse("익명의회원");

        Context context = new Context();
        context.setVariable("nickname", nickname);

        return templateEngine.process("mail/signup-complete-template", context);
    }

    @Override
    public EmailType getType() {
        return EmailType.WELCOME;
    }

    @Override
    public CompletableFuture<Void> send(String email) {
        return super.send(email);
    }

}
