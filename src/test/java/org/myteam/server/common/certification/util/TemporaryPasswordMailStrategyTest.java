package org.myteam.server.common.certification.util;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.common.certification.mail.strategy.TemporaryPasswordMailStrategy;
import org.myteam.server.common.certification.mail.util.CertifyStorage;
import org.myteam.server.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TemporaryPasswordMailStrategyTest extends IntegrationTestSupport{

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private SpringTemplateEngine templateEngine;

    private TemporaryPasswordMailStrategy tempPasswordStrategy;

    @BeforeEach
    void setUp() {
        tempPasswordStrategy = new TemporaryPasswordMailStrategy(
                javaMailSender, memberJpaRepository, passwordEncoder, templateEngine
        );
        ReflectionTestUtils.setField(tempPasswordStrategy, "senderEmail", "test@test.com");
    }

    @Test
    @DisplayName("임시 비밀번호 메일 발송 성공")
    void sendTemporaryPassword_success() throws Exception {
        // given
        String email = "test@example.com";
        Member member = createMember(1);

        when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        when(templateEngine.process(eq("mail/temporary-password-template"), any(Context.class)))
                .thenReturn("123456");

        // when
        tempPasswordStrategy.send(email);

        // then
        verify(javaMailSender).send(any(MimeMessage.class));
    }
}