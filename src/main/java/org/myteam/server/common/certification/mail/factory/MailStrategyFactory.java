package org.myteam.server.common.certification.mail.factory;

import lombok.RequiredArgsConstructor;
<<<<<<< Updated upstream
=======
import org.myteam.server.common.certification.mail.strategy.CertifyMailStrategy;
import org.myteam.server.common.certification.mail.strategy.NotifySuspendStrategy;
import org.myteam.server.common.certification.mail.strategy.SignUpStrategy;
import org.myteam.server.common.certification.mail.strategy.TemporaryPasswordMailStrategy;
>>>>>>> Stashed changes
import org.myteam.server.common.certification.mail.domain.EmailType;
import org.myteam.server.common.certification.mail.core.MailStrategy;
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
            mailStrategyMap.put(strategy.getType(), strategy);
        });
    }

    public MailStrategy getStrategy(EmailType type) {
        return Optional.ofNullable(mailStrategyMap.get(type))
                .orElseThrow(() -> new PlayHiveException(ErrorCode.NOT_SUPPORT_TYPE));
    }
}