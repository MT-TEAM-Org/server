package org.myteam.server.common.certification.util;

import lombok.extern.slf4j.Slf4j;
import org.myteam.server.common.certification.domain.CertificationCode;
import org.myteam.server.common.mail.service.AbstractMailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Objects;


@Slf4j
@Component
public class CertifyMailStrategy extends AbstractMailSender {
    private final CertifyStorage certifyStorage;

    public CertifyMailStrategy(JavaMailSender javaMailSender, CertifyStorage certifyStorage, SpringTemplateEngine templateEngine) {
        super(javaMailSender, templateEngine);
        this.certifyStorage = certifyStorage;
    }

    @Override
    protected String getSubject() {
        return "PlayHive 인증 메일";
    }

    @Override
    protected String getBody(String email) {
        CertificationCode certificationCode = certifyStorage.putCertificationCode(email);
        return buildEmailContent(certificationCode.getCode(), email);
    }

    // 인증번호가 유효한지 검사한다.
    @Override
    public boolean verify(String email, String inputCode) {
        log.debug("검증 중.......");
        CertificationCode storedCode = certifyStorage.getCertificationCode(email);
        log.debug("stored code {}", storedCode.getCode());
        log.debug("inputCode {}", inputCode);

        boolean isValid = Objects.equals(storedCode.getCode(), inputCode); // 코드가 일치하면 유효

        if (isValid) {
            try {
                certifyStorage.deleteCertificationCode(email);
            } catch (Exception e) {
                log.error("❌ 인증 코드 삭제 실패 - email: {}", email, e);
            }
            certifyStorage.putCertifiedEmail(email);
        }

        return isValid;
    }

    // 메일 내용을 작성한다.
    public String buildEmailContent(String code, String email) {
        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("email", email);

        return templateEngine.process("mail/certify-template", context);
    }
}
