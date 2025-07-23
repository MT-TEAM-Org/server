package org.myteam.server.common.certification.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.common.certification.mail.core.MailStrategy;
import org.myteam.server.common.certification.mail.domain.EmailType;
import org.myteam.server.common.certification.mail.factory.MailStrategyFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuspendMailSendService {


    private final MailStrategyFactory mailStrategyFactory;

    public void sendAdminSuspendMail(String email,String ip){
        MailStrategy strategy = mailStrategyFactory.getStrategy(EmailType.NOTIFY_ADMIN_SUSPEND_GLOBAL);
        strategy.send(email+">"+ip);

        strategy=mailStrategyFactory.getStrategy(EmailType.NOTIFY_ADMIN_SUSPEND);
        strategy.send(email);
    }
}
