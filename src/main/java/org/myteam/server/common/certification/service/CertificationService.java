package org.myteam.server.common.certification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.common.certification.mail.core.MailStrategy;
import org.myteam.server.common.certification.mail.domain.EmailType;
import org.myteam.server.common.certification.mail.factory.MailStrategyFactory;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CertificationService {
    private final MailStrategyFactory mailStrategyFactory;

    public void send(String email) {
        log.info("{} 메일 전송 시작 - email: {}", email);
        MailStrategy strategy = mailStrategyFactory.getStrategy(EmailType.CERTIFICATION);
        strategy.send(email);
    }

    public boolean certify(String email, String certificationCode) {
        log.info("이메일 인증 코드 검증 - email: {}", email);

        MailStrategy strategy = mailStrategyFactory.getStrategy(EmailType.CERTIFICATION);

        log.info("MailStrategy 구현체 타입: {}", strategy.getClass().getName());

        boolean isVerified = strategy.verify(email, certificationCode);

        if (!isVerified) {
            throw new PlayHiveException(ErrorCode.NOT_SUPPORT_TYPE);
        }

        return true;
    }
}
