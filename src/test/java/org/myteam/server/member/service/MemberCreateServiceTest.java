package org.myteam.server.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.common.certification.mail.strategy.TemporaryPasswordMailStrategy;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.common.certification.mail.core.MailStrategy;
import org.myteam.server.common.certification.mail.domain.EmailType;
import org.myteam.server.common.certification.mail.util.CertifyStorage;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.oauth2.dto.AddMemberInfoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Transactional
class MemberCreateServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;
    @MockBean
    private CertifyStorage certifyStorage;

    private MemberSaveRequest saveRequest;

    @BeforeEach
    void setUp() {
        saveRequest = MemberSaveRequest.builder()
                .email("test@test.com")
                .password("password1234!")
                .nickname("testNickname")
                .tel("01012345678")
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void createMember_success() {
        // when
        MailStrategy mockStrategy = mock(MailStrategy.class);
        when(mailStrategyFactory.getStrategy(EmailType.WELCOME)).thenReturn(mockStrategy);
        doReturn(CompletableFuture.completedFuture(null))
                .when(mockStrategy).send(anyString());

        MemberResponse response = memberService.create(saveRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(saveRequest.getEmail());
        assertThat(response.getNickname()).isEqualTo(saveRequest.getNickname());
    }

    @Test
    @DisplayName("이미 가입된 이메일로 회원가입 시도 → 예외 발생")
    void createMember_alreadyExists() {
        // given
        MailStrategy mockStrategy = mock(MailStrategy.class);
        when(mailStrategyFactory.getStrategy(EmailType.WELCOME)).thenReturn(mockStrategy);
        doReturn(CompletableFuture.completedFuture(null))
                .when(mockStrategy).send(anyString());

        memberService.create(saveRequest);

        // when & then
        assertThatThrownBy(() -> memberService.create(saveRequest))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.USER_ALREADY_EXISTS.getMsg());
    }

    @Test
    @DisplayName("소셜 로그인 추가정보 입력 성공")
    void addInfo_success() {
        // given
        MailStrategy mockStrategy = mock(MailStrategy.class);
        when(mailStrategyFactory.getStrategy(EmailType.WELCOME)).thenReturn(mockStrategy);
        doReturn(CompletableFuture.completedFuture(null))
                .when(mockStrategy).send(anyString());

        Member member = createOAuthMember(1);
        AddMemberInfoRequest request = AddMemberInfoRequest.builder()
                .email(member.getEmail())
                .memberType(MemberType.KAKAO)
                .tel("01098765432")
                .nickname("newNickname")
                .build();

        // when
        memberService.addInfo(request);

        // then
        Member updated = memberJpaRepository.findByPublicId(member.getPublicId()).get();
        assertThat(updated.getNickname()).isEqualTo("newNickname");
        assertThat(updated.getTel()).isEqualTo("01098765432");
        assertThat(updated.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("소셜 로그인 추가정보 - 존재하지 않는 사용자 → 예외 발생")
    void addInfo_userNotFound() {
        // given
        MailStrategy mockStrategy = mock(MailStrategy.class);
        when(mailStrategyFactory.getStrategy(EmailType.WELCOME)).thenReturn(mockStrategy);
        doReturn(CompletableFuture.completedFuture(null))
                .when(mockStrategy).send(anyString());

        AddMemberInfoRequest request = AddMemberInfoRequest.builder()
                .email("notExist@test.com")
                .memberType(MemberType.KAKAO)
                .tel("01000000000")
                .nickname("nickname")
                .build();

        // when & then
        assertThatThrownBy(() -> memberService.addInfo(request))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMsg());
    }

    @Test
    @DisplayName("소셜 로그인 추가정보 - 권한 없는 상태에서 수정 시도 → 예외 발생")
    void addInfo_unauthorizedStatus() {
        // given
        MailStrategy mockStrategy = mock(MailStrategy.class);
        when(mailStrategyFactory.getStrategy(EmailType.WELCOME)).thenReturn(mockStrategy);
        doReturn(CompletableFuture.completedFuture(null))
                .when(mockStrategy).send(anyString());

        Member member = createMember(1); // ACTIVE 상태로 생성됨

        AddMemberInfoRequest request = AddMemberInfoRequest.builder()
                .email("notExist@test.com")
                .memberType(MemberType.LOCAL)
                .tel("01000000000")
                .nickname("nickname")
                .build();

        // when & then
        assertThatThrownBy(() -> memberService.addInfo(request))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMsg());
    }

    @Test
    @DisplayName("소셜 로그인 추가정보 - 권한 없는 상태에서 수정 시도 → 예외 발생")
    void addInfo_unauthorized_pendingStatus() {
        // given
        MailStrategy mockStrategy = mock(MailStrategy.class);
        when(mailStrategyFactory.getStrategy(EmailType.WELCOME)).thenReturn(mockStrategy);
        doReturn(CompletableFuture.completedFuture(null))
                .when(mockStrategy).send(anyString());

        Member member = createOAuthMemberNonPending(1); // ACTIVE 상태로 생성됨

        AddMemberInfoRequest request = AddMemberInfoRequest.builder()
                .email(member.getEmail())
                .memberType(member.getType())
                .tel("01000000000")
                .nickname("nickname")
                .build();

        // when & then
        assertThatThrownBy(() -> memberService.addInfo(request))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.UNAUTHORIZED.getMsg());
    }

    @Test
    @DisplayName("임시 비밀번호 발급 성공")
    void generateTemporaryPassword_success() {
        // given
        MailStrategy mockStrategy = mock(MailStrategy.class);
        when(mailStrategyFactory.getStrategy(EmailType.TEMPORARY_PASSWORD)).thenReturn(mockStrategy);
        doReturn(CompletableFuture.completedFuture(null))
                .when(mockStrategy).send(anyString());

        String email = "test@test.com";
        when(certifyStorage.isCertified(email)).thenReturn(true);
        when(mailStrategyFactory.getStrategy(EmailType.TEMPORARY_PASSWORD))
                .thenReturn(mockStrategy); // 메일전략 그냥 무시

        // when & then
        assertThatCode(() -> memberService.generateTemporaryPassword(email))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("임시 비밀번호 발급 실패 - 인증 안된 경우")
    void generateTemporaryPassword_fail_notCertified() {
        // given
        MailStrategy mockStrategy = mock(MailStrategy.class);
        when(mailStrategyFactory.getStrategy(EmailType.TEMPORARY_PASSWORD)).thenReturn(mockStrategy);
        doReturn(CompletableFuture.completedFuture(null))
                .when(mockStrategy).send(anyString());

        String email = "test@test.com";
        when(certifyStorage.isCertified(email)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> memberService.generateTemporaryPassword(email))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.UNAUTHORIZED_EMAIL_ACCOUNT.getMsg());
    }
}