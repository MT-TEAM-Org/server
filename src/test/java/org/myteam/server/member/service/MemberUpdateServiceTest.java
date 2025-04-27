package org.myteam.server.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.dto.MemberRoleUpdateRequest;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.dto.PasswordChangeRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.oauth2.dto.AddMemberInfoRequest;
import org.myteam.server.profile.dto.request.ProfileRequestDto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@Transactional
class MemberUpdateServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;

    private Member member;
    private Member nonAuthMember;
    private Member serviceMember;

    @BeforeEach
    void setUp() {
        member = createMember(1);
        nonAuthMember = createNonAuthMember(2);
        serviceMember = createMemberByService(3);
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


}