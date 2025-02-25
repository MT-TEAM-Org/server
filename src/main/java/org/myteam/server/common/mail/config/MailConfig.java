package org.myteam.server.common.mail.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("smtp.naver.com");
        mailSender.setPort(587);
        mailSender.setUsername("teamplayhive@naver.com");
        mailSender.setPassword("Play!57304");

        return mailSender;
    }
}
