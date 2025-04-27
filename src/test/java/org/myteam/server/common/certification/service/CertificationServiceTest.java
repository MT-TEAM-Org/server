package org.myteam.server.common.certification.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.common.certification.util.CertifyMailStrategy;
import org.myteam.server.common.certification.util.TemporaryPasswordMailStrategy;
import org.myteam.server.common.mail.domain.EmailType;
import org.myteam.server.common.mail.service.MailStrategy;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CertificationServiceTest extends IntegrationTestSupport {

    @Autowired
    private CertificationService certificationService;

    @Test
    @DisplayName("send(): 전략에 따라 메일 전송이 호출된다")
    void send_success() {
        // given
        String email = "test@example.com";
        EmailType type = EmailType.CERTIFICATION;

        MailStrategy mockStrategy = mock(MailStrategy.class);
        when(mailStrategyFactory.getStrategy(type)).thenReturn(mockStrategy);

        // when
        certificationService.send(email, type);

        // then
        verify(mockStrategy).send(email);
    }

    @Test
    @DisplayName("certify(): 인증 코드 검증 성공")
    void certify_success() {
        // given
        String email = "test@example.com";
        String code = "123456";

        CertifyMailStrategy mockCertifyStrategy = mock(CertifyMailStrategy.class);
        when(mailStrategyFactory.getStrategy(EmailType.CERTIFICATION)).thenReturn(mockCertifyStrategy);
        when(mockCertifyStrategy.verify(email, code)).thenReturn(true);

        // when
        boolean result = certificationService.certify(email, code);

        // then
        assertThat(result).isTrue();
        verify(mockCertifyStrategy).verify(email, code);
    }

    @Test
    @DisplayName("certify(): 인증 타입이 CertifyMailStrategy가 아니면 예외 발생")
    void certify_wrongStrategy_throw() {
        // given
        String email = "test@example.com";
        String code = "123456";

        MailStrategy wrongStrategy = mock(TemporaryPasswordMailStrategy.class);
        when(mailStrategyFactory.getStrategy(EmailType.CERTIFICATION)).thenReturn(wrongStrategy);

        // when & then
        assertThatThrownBy(() -> certificationService.certify(email, code))
                .isInstanceOf(PlayHiveException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_SUPPORT_TYPE);
    }
}