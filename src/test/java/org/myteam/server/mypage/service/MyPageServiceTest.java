package org.myteam.server.mypage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.domain.GenderType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.mypage.dto.request.MyPageRequest.*;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class MyPageServiceTest extends IntegrationTestSupport {

    @Autowired
    private MyPageService myPageService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = createMember(1);
        when(securityReadService.getMember()).thenReturn(member);
    }

    @Test
    @DisplayName("1. 모든 필드 정상 수정 성공")
    void updateMemberInfo_allFields_success() {
        // given
        MyPageUpdateRequest request = MyPageUpdateRequest.builder()
                .email(member.getEmail())
                .password("newPassword")
                .tel("01099998888")
                .nickname("newNickname")
                .imageUrl("http://new.image.url")
                .birthDate("900101")
                .genderType(GenderType.M)
                .build();

        // when
        myPageService.updateMemberInfo(request);

        // then
        Member updatedMember = memberJpaRepository.findByEmail(member.getEmail()).get();
        assertThat(updatedMember.getNickname()).isEqualTo("newNickname");
        assertThat(updatedMember.getTel()).isEqualTo("01099998888");
        assertThat(updatedMember.getGenderType()).isEqualTo(GenderType.M);
    }

    @Test
    @DisplayName("2. 비밀번호 없이 다른 정보만 수정 성공")
    void updateMemberInfo_withoutPassword_success() {
        // given
        MyPageUpdateRequest request = MyPageUpdateRequest.builder()
                .email(member.getEmail())
                .password(null) // 비밀번호는 수정 안 함
                .tel("01012345678")
                .nickname("updatedNickname")
                .imageUrl("http://updated.image.url")
                .birthDate("000505")
                .genderType(GenderType.F)
                .build();

        // when
        myPageService.updateMemberInfo(request);

        // then
        Member updatedMember = memberJpaRepository.findByEmail(member.getEmail()).get();
        assertThat(updatedMember.getNickname()).isEqualTo("updatedNickname");
        assertThat(updatedMember.getGenderType()).isEqualTo(GenderType.F);
    }

    @Test
    @DisplayName("3. 생년월일이 null인 경우 생년월일 업데이트 생략")
    void updateMemberInfo_withoutBirthDate_success() {
        // given
        MyPageUpdateRequest request = MyPageUpdateRequest.builder()
                .email(member.getEmail())
                .birthDate(null)
                .nickname("newNickname")
                .build();

        // when
        myPageService.updateMemberInfo(request);

        // then
        Member updatedMember = memberJpaRepository.findByEmail(member.getEmail()).get();
        assertThat(updatedMember.getNickname()).isEqualTo("newNickname");
        assertThat(updatedMember.getBirthDay()).isEqualTo(0);
    }

    @Test
    @DisplayName("4. 성별이 null인 경우 성별 업데이트 생략")
    void updateMemberInfo_withoutGender_success() {
        // given
        MyPageUpdateRequest request = MyPageUpdateRequest.builder()
                .email(member.getEmail())
                .genderType(null)
                .nickname("nicknameWithoutGender")
                .build();

        // when
        myPageService.updateMemberInfo(request);

        // then
        Member updatedMember = memberJpaRepository.findByEmail(member.getEmail()).get();
        assertThat(updatedMember.getNickname()).isEqualTo("nicknameWithoutGender");
    }

    @Test
    @DisplayName("5. 이메일 불일치 → 예외 발생")
    void updateMemberInfo_invalidEmail_throw() {
        // given
        MyPageUpdateRequest request = MyPageUpdateRequest.builder()
                .email("invalid@email.com") // 본인 이메일 아님
                .nickname("nickname")
                .build();

        // when & then
        assertThatThrownBy(() -> myPageService.updateMemberInfo(request))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.UNAUTHORIZED.getMsg());
    }
}