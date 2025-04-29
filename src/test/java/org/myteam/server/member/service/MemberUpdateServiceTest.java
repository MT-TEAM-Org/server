package org.myteam.server.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.common.certification.mail.core.MailStrategy;
import org.myteam.server.common.certification.mail.domain.EmailType;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.dto.MemberRoleUpdateRequest;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.dto.MemberStatusUpdateRequest;
import org.myteam.server.member.dto.PasswordChangeRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.oauth2.dto.AddMemberInfoRequest;
import org.myteam.server.profile.dto.request.ProfileRequestDto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@Transactional
class MemberUpdateServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;

    private Member member;
    private Member nonAuthMember;
    private Member serviceMember;
    private final String NON_EXISTS = "nonExist@email.com";

    private Member requester;  // 상태 바꾸려는 사람 (요청자)
    private Member target;     // 바뀌는 대상자
    private Member admin;

    @BeforeEach
    void setUp() {
        member = createMember(1);
        nonAuthMember = createNonAuthMember(2);
        serviceMember = createMemberByService(3);

        requester = createMember(4);
        target = createMember(5);
        admin = createAdmin(6);
    }

    @Test
    @DisplayName("1. 회원 프로필 수정 성공")
    void updateMemberProfile_success() {
        // given
        when(securityReadService.getMember()).thenReturn(member);

        MemberUpdateRequest updateRequest = MemberUpdateRequest.builder()
                .email(member.getEmail())
                .nickname("newNickname")
                .password("newPassword!")
                .tel("01099998888")
                .build();

        // when
        MemberResponse response = memberService.updateMemberProfile(updateRequest);

        // then
        assertThat(response.getNickname()).isEqualTo("newNickname");
    }

    @Test
    @DisplayName("2. 다른 이메일로 수정 요청 시 → 예외 발생")
    void updateMemberProfile_emailMismatch() {
        // given
        when(securityReadService.getMember()).thenReturn(member);

        MemberUpdateRequest updateRequest = new MemberUpdateRequest(
                "wrong@test.com", "nickname", "01011112222", "password!"
        );

        // when & then
        assertThatThrownBy(() -> memberService.updateMemberProfile(updateRequest))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.NO_PERMISSION.getMsg());
    }

    @Test
    @DisplayName("3. 회원 비밀번호 변경 성공")
    void changePassword_success() {
        // given
        PasswordChangeRequest changeRequest = PasswordChangeRequest.builder()
                .password("1234")
                .newPassword("newPassword1234!")
                .confirmPassword("newPassword1234!")
                .build();

        // when
        memberService.changePassword(serviceMember.getEmail(), changeRequest);

        // then
        Member updated = memberJpaRepository.findByEmail(serviceMember.getEmail()).orElseThrow();
        assertThat(updated.getPassword()).isNotEqualTo("1234"); // 인코딩되었기 때문에 달라야 함
    }

    @Test
    @DisplayName("4. 새 비밀번호와 확인 비밀번호가 다르면 예외 발생")
    void changePassword_mismatchConfirmPassword() {
        // given
        PasswordChangeRequest changeRequest = PasswordChangeRequest.builder()
                .password("1234")
                .newPassword("newPassword1234!")
                .confirmPassword("wrongConfirm")
                .build();

        // when & then
        assertThatThrownBy(() -> memberService.changePassword(member.getEmail(), changeRequest))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("5. 현재 비밀번호가 일치하지 않으면 예외 발생")
    void changePassword_invalidCurrentPassword() {
        // given
        PasswordChangeRequest changeRequest = PasswordChangeRequest.builder()
                .password("wrongPassword")
                .newPassword("newPassword1234!")
                .confirmPassword("newPassword1234!")
                .build();

        // when & then
        assertThatThrownBy(() -> memberService.changePassword(member.getEmail(), changeRequest))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage("현재 비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("6. 회원 타입(Role) 변경 성공")
    void updateRole_success() {
        // given
        MemberRoleUpdateRequest roleUpdateRequest = MemberRoleUpdateRequest.builder()
                .email(member.getEmail())
                .role(MemberRole.ADMIN)
                .publicId(member.getPublicId())
                .type(MemberType.LOCAL)
                .build();

        // when
        MemberResponse response = memberService.updateRole(roleUpdateRequest);

        // then
        assertThat(response.getRole()).isEqualTo(MemberRole.ADMIN);
    }

    @Test
    @DisplayName("7. 회원 타입(Role) 변경 실패 - 유저 정보 없음")
    void updateRole_fail() {
        // given
        MemberRoleUpdateRequest roleUpdateRequest = MemberRoleUpdateRequest.builder()
                .email(NON_EXISTS)
                .role(MemberRole.ADMIN)
                .publicId(member.getPublicId())
                .type(MemberType.LOCAL)
                .build();

        // when & then
        assertThatThrownBy(() ->  memberService.updateRole(roleUpdateRequest))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMsg());
    }

    @Transactional
    @Test
    @DisplayName("1. 요청자가 본인 상태를 변경 (PENDING 상태일 때만 가능)")
    void updateStatus_self_pending_success() {
        // given
        requester.updateStatus(MemberStatus.PENDING);
        memberJpaRepository.save(requester);

        MemberStatusUpdateRequest request = MemberStatusUpdateRequest.builder()
                .email(requester.getEmail())
                .status(MemberStatus.ACTIVE)
                .build();

        // when
        memberService.updateStatus(requester.getEmail(), request);

        // then
        assertThat(memberJpaRepository.findByEmail(requester.getEmail()).get().getStatus())
                .isEqualTo(MemberStatus.ACTIVE);
    }

    @Transactional
    @Test
    @DisplayName("2. 요청자가 본인 상태를 변경 (PENDING이 아닌 경우 예외 발생)")
    void updateStatus_self_notPending_throw() {
        // given
        requester.updateStatus(MemberStatus.ACTIVE);
        memberJpaRepository.save(requester);

        MemberStatusUpdateRequest request = MemberStatusUpdateRequest.builder()
                .email(requester.getEmail())
                .status(MemberStatus.ACTIVE)
                .build();

        // when & then
        assertThatThrownBy(() -> memberService.updateStatus(requester.getEmail(), request))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.NO_PERMISSION.getMsg());
    }

    @Transactional
    @Test
    @DisplayName("3. 관리자가 다른 사람 상태를 변경 성공")
    void updateStatus_admin_changeOthers_success() {
        // given
        MemberStatusUpdateRequest request = MemberStatusUpdateRequest.builder()
                .email(target.getEmail())
                .status(MemberStatus.INACTIVE)
                .build();

        // when
        memberService.updateStatus(admin.getEmail(), request);

        // then
        assertThat(memberJpaRepository.findByEmail(target.getEmail()).get().getStatus())
                .isEqualTo(MemberStatus.INACTIVE);
    }

    @Test
    @DisplayName("4. 권한 없는 사용자가 다른 사람 상태 변경 시도 → 예외 발생")
    void updateStatus_user_changeOthers_throw() {
        // given
        MemberStatusUpdateRequest request = MemberStatusUpdateRequest.builder()
                .email(target.getEmail())
                .status(MemberStatus.INACTIVE)
                .build();

        // when & then
        assertThatThrownBy(() -> memberService.updateStatus(requester.getEmail(), request))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage("상태 수정 권한이 없습니다.");
    }

    @Test
    @DisplayName("5. 대상 사용자가 존재하지 않으면 예외 발생")
    void updateStatus_targetNotFound_throw() {
        // given
        MemberStatusUpdateRequest request = MemberStatusUpdateRequest.builder()
                .email(NON_EXISTS)
                .status(MemberStatus.INACTIVE)
                .build();

        // when & then
        assertThatThrownBy(() -> memberService.updateStatus(requester.getEmail(), request))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMsg());
    }

    @Test
    @DisplayName("6. 요청자 이메일이 잘못된 경우 (존재하지 않음) 예외 발생")
    void updateStatus_requesterNotFound_throw() {
        // given
        MemberStatusUpdateRequest request = MemberStatusUpdateRequest.builder()
                .email(target.getEmail())
                .status(MemberStatus.INACTIVE)
                .build();

        // when & then
        assertThatThrownBy(() -> memberService.updateStatus(NON_EXISTS, request))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMsg());
    }
}