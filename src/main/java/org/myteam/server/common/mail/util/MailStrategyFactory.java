package org.myteam.server.common.mail.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.common.certification.util.CertifyMailStrategy;
import org.myteam.server.common.certification.util.SignUpStrategy;
import org.myteam.server.common.certification.util.TemporaryPasswordMailStrategy;
import org.myteam.server.common.mail.domain.EmailType;
import org.myteam.server.common.mail.service.MailStrategy;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MailStrategyFactory {

    private final Map<EmailType, MailStrategy> mailStrategyMap = new HashMap<>();

    @Autowired
    public void init(Map<String, MailStrategy> strategies) {
        strategies.forEach((key, strategy) -> {
            if (strategy instanceof CertifyMailStrategy) {
                mailStrategyMap.put(EmailType.CERTIFICATION, strategy);
            } else if (strategy instanceof TemporaryPasswordMailStrategy) {
                mailStrategyMap.put(EmailType.TEMPORARY_PASSWORD, strategy);
            } else if (strategy instanceof SignUpStrategy) {
                mailStrategyMap.put(EmailType.WELCOME, strategy);
            }
        });
    }

    public MailStrategy getStrategy(EmailType type) {
        return Optional.ofNullable(mailStrategyMap.get(type))
                .orElseThrow(() -> new PlayHiveException(ErrorCode.NOT_SUPPORT_TYPE));
    }
}