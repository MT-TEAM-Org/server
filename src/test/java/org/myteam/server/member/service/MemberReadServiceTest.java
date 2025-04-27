package org.myteam.server.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardRecommend;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.dto.reponse.BoardResponse;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.dto.FindIdResponse;
import org.myteam.server.member.entity.Member;
import org.myteam.server.profile.dto.response.ProfileResponseDto.*;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Transactional
class MemberReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberReadService memberReadService;

    @MockBean
    private JwtProvider jwtProvider;

    private Member member;
    private Member nonAuthMember;

    @BeforeEach
    void setUp() {
        member = createMember(1);
        nonAuthMember = createNonAuthMember(10);
        memberJpaRepository.flush();
    }

    @Test
    @DisplayName("1. publicId로 멤버 조회 성공")
    void findById_success() {
        assertThat(memberReadService.findById(member.getPublicId()))
                .isNotNull()
                .extracting(Member::getEmail)
                .isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("non status 멤버 조회")
    void findById_fail_non_auth() {
        assertThatThrownBy(() -> memberReadService.findById(nonAuthMember.getPublicId()))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.INVALID_USER.getMsg());
    }

    @Test
    @DisplayName("2. 존재하지 않는 publicId로 멤버 조회시 예외")
    void findById_fail() {
        assertThatThrownBy(() -> memberReadService.findById(UUID.randomUUID()))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMsg());
    }

    @Test
    @DisplayName("3. 이메일로 멤버 조회 성공")
    void findByEmail_success() {
        assertThat(memberReadService.findByEmail(member.getEmail()))
                .isNotNull()
                .extracting(Member::getEmail)
                .isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("4. 존재하지 않는 이메일로 조회시 예외")
    void findByEmail_fail() {
        assertThatThrownBy(() -> memberReadService.findByEmail("noexist@example.com"))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMsg());
    }

    @Test
    @DisplayName("5. 내 프로필 조회 성공")
    void getProfile_success() {
        when(securityReadService.getMember()).thenReturn(member);

        assertThat(memberReadService.getProfile())
                .isNotNull()
                .extracting(ProfileResponse::getNickname)
                .isEqualTo(member.getNickname());
    }

    @Test
    @DisplayName("6. publicId로 MemberResponse 조회 성공")
    void getByPublicId_success() {
        assertThat(memberReadService.getByPublicId(member.getPublicId()))
                .isNotNull()
                .extracting(MemberResponse::getNickname)
                .isEqualTo(member.getNickname());
    }

    @Test
    @DisplayName("7. 이메일로 MemberResponse 조회 성공")
    void getByEmail_success() {
        assertThat(memberReadService.getByEmail(member.getEmail()))
                .isNotNull()
                .extracting(MemberResponse::getNickname)
                .isEqualTo(member.getNickname());
    }

    @Test
    @DisplayName("8. 존재하지 않는 이메일 조회시 예외 발생")
    void getByEmail_fail() {
        assertThatThrownBy(() -> memberReadService.getByEmail("none@example.com"))
                .isInstanceOf(PlayHiveException.class)
                .hasMessageContaining("는 존재하지 않는 이메일 입니다");
    }

    @Test
    @DisplayName("9. 닉네임으로 MemberResponse 조회 성공")
    void getByNickname_success() {
        assertThat(memberReadService.getByNickname(member.getNickname()))
                .isNotNull()
                .extracting(MemberResponse::getEmail)
                .isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("10. 존재하지 않는 닉네임 조회시 예외 발생")
    void getByNickname_fail() {
        assertThatThrownBy(() -> memberReadService.getByNickname("없는닉네임"))
                .isInstanceOf(PlayHiveException.class)
                .hasMessageContaining("는 존재하지 않는 닉네임 입니다");
    }

    @Test
    @DisplayName("11. 전체 회원 목록 조회")
    void list_success() {
        List<Member> list = memberReadService.list();
        assertThat(list).isNotEmpty();
    }

    @Test
    @DisplayName("12. 닉네임 존재 여부 조회 성공")
    void existsByNickname_success() {
        assertThat(memberReadService.existsByNickname(member.getNickname()))
                .isTrue();
    }

    @Test
    @DisplayName("13. publicId로 현재 로그인한 사용자 이메일 조회")
    void getCurrentLoginUserEmail_success() {
        assertThat(memberReadService.getCurrentLoginUserEmail(member.getPublicId()))
                .isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("14. Authorization Header에서 Member 조회 성공")
    void getAuthenticatedMember_success() {
        String authorizationHeader = "Bearer access.token.value";
        UUID publicId = member.getPublicId();

        when(jwtProvider.getAccessToken(anyString())).thenReturn("access.token.value");
        when(jwtProvider.getPublicId(anyString())).thenReturn(publicId);

        MemberResponse response = memberReadService.getAuthenticatedMember(authorizationHeader);

        assertThat(response.getNickname()).isEqualTo(member.getNickname());
    }

    @Test
    @DisplayName("15. Authorization Header 없거나 이상할 때 예외 발생")
    void getAuthenticatedMember_fail() {
        assertThatThrownBy(() -> memberReadService.getAuthenticatedMember(null))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.NO_PERMISSION.getMsg());

        assertThatThrownBy(() -> memberReadService.getAuthenticatedMember("invalid.token"))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.NO_PERMISSION.getMsg());
    }

    @Test
    @DisplayName("16. 이메일로 MemberType 조회")
    void getMemberTypeByEmail_success() {
        assertThat(memberReadService.getMemberTypeByEmail(member.getEmail()))
                .isEqualTo(member.getType());
    }

    @Test
    @DisplayName("17. 존재하지 않는 이메일로 MemberType 조회시 null 반환")
    void getMemberTypeByEmail_fail() {
        assertThat(memberReadService.getMemberTypeByEmail("none@example.com"))
                .isNull();
    }

    @Transactional
    @Test
    @DisplayName("18. 전화번호로 아이디 찾기 성공")
    void findUserId_success() {
        createMember(100);
        FindIdResponse response = memberReadService.findUserId("01012345678");

        assertThat(response).isNotNull();
        assertThat(response.getMembers()).isNotEmpty();
    }

    @Test
    @DisplayName("19. 전화번호 길이가 잘못된 경우 예외 발생")
    void findUserId_invalidPhone() {
        assertThatThrownBy(() -> memberReadService.findUserId("012345"))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.INVALID_PHONE_NUMBER.getMsg());
    }

    @Test
    @DisplayName("20. 전화번호로 사용자 찾지 못한 경우 예외 발생")
    void findUserId_userNotFound() {
        assertThatThrownBy(() -> memberReadService.findUserId("01099999999"))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMsg());
    }
}