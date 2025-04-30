package org.myteam.server.common.certification.mail.strategy;

import lombok.extern.slf4j.Slf4j;
import org.myteam.server.common.certification.mail.core.AbstractMailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Component
public class SignUpStrategy extends AbstractMailSender {

    public SignUpStrategy(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        super(javaMailSender, templateEngine);
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
        Context context = new Context();
        context.setVariable("email", email);

        return templateEngine.process("mail/signup-complete-template", context);
    }
}
