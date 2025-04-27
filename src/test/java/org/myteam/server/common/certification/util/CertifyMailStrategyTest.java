package org.myteam.server.common.certification.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.common.certification.domain.CertificationCode;
import org.myteam.server.common.certification.mail.strategy.CertifyMailStrategy;
import org.myteam.server.common.certification.mail.util.CertifyStorage;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CertifyMailStrategyTest extends IntegrationTestSupport{

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private CertifyStorage certifyStorage;

    @MockBean
    private SpringTemplateEngine templateEngine;

    private CertifyMailStrategy certifyMailStrategy;

    @BeforeEach
    void setUp() {
        certifyMailStrategy = new CertifyMailStrategy(
                javaMailSender, certifyStorage, templateEngine
        );
    }

    @Test
    @DisplayName("인증 코드 검증 성공")
    void verifyCertificationCode_success() {
        // given
        String email = "test@example.com";
        String code = "123456";

        when(certifyStorage.getCertificationCode(email))
                .thenReturn(new CertificationCode(code, LocalDateTime.now().plusMinutes(5L)));

        // when
        boolean result = certifyMailStrategy.verify(email, code);

        // then
        assertThat(result).isTrue();
        verify(certifyStorage).deleteCertificationCode(email);
        verify(certifyStorage).putCertifiedEmail(email);
    }

    @Test
    @DisplayName("인증 코드 검증 실패")
    void verifyCertificationCode_fail() {
        // given
        String email = "test@example.com";
        String code = "123456";
        String wrongCode = "654321";

        when(certifyStorage.getCertificationCode(email))
                .thenReturn(new CertificationCode(code, LocalDateTime.now()));

        // when
        boolean result = certifyMailStrategy.verify(email, wrongCode);

        // then
        assertThat(result).isFalse();
    }
}