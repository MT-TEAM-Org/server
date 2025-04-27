package org.myteam.server.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@Transactional
class MemberDeleteServiceTest  extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = createMember(1);
    }

    @Test
    @DisplayName("1. 회원 탈퇴 성공 - 상태가 INACTIVE로 변경되고 토큰 삭제됨")
    void deleteMember_success() {
        // given
        when(securityReadService.getMember()).thenReturn(member);

        // when
        memberService.deleteMember();

        // then
        Member withdrawnMember = memberJpaRepository.findByPublicId(member.getPublicId()).orElseThrow();
        assertThat(withdrawnMember.getStatus()).isEqualTo(MemberStatus.INACTIVE);
    }

    @Test
    @DisplayName("2. 특정 이메일로 회원 삭제 성공")
    void deleteByEmail_success() {
        // when
        memberService.delete(member.getEmail());

        // then
        Optional<Member> deleted = memberJpaRepository.findByEmail(member.getEmail());
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("3. 존재하지 않는 이메일로 삭제 요청 시 예외 발생")
    void deleteByEmail_userNotFound() {
        // given
        String wrongEmail = "notfound@test.com";

        // when & then
        assertThatThrownBy(() -> memberService.delete(wrongEmail))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMsg());
    }
}