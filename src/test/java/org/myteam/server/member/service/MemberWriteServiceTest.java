package org.myteam.server.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.profile.dto.request.ProfileRequestDto.MemberUpdateRequest;
import org.myteam.server.profile.dto.request.ProfileRequestDto.MemberDeleteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.profile.dto.request.ProfileRequestDto.MemberUpdateRequest;
import org.myteam.server.profile.dto.request.ProfileRequestDto.MemberDeleteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.myteam.server.global.exception.ErrorCode.USER_ALREADY_EXISTS;


class MemberWriteServiceTest  extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberReadService memberReadService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Member member;

    @Test
    @DisplayName("✅ 회원 가입 성공")
    @Transactional
    void createMember_Success() {
        // Given
        MemberSaveRequest request = MemberSaveRequest.builder()
                .email("test@example.com")
                .password("password123")
                .tel("01012345678")
                .nickname("testUser")
                .build();

        // When
        when(memberJpaRepository.findByEmail(request.getEmail()))
                .thenThrow(new PlayHiveException(ErrorCode.USER_NOT_FOUND));
        MemberResponse response = memberService.create(request);

        // Then
        assertNotNull(response);
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(MemberStatus.ACTIVE, response.getStatus());
    }

    @Test
    @DisplayName("❌ 회원 가입 실패 - 이메일 중복")
    void createMember_Failure_DuplicateEmail() {
        // Given
        MemberSaveRequest request = MemberSaveRequest.builder()
                .email("test@example.com")
                .password("password123")
                .tel("01012345678")
                .nickname("testUser")
                .build();
        MemberResponse response = memberService.create(request);

        // When & Then
        PlayHiveException exception = assertThrows(PlayHiveException.class, () -> {
            memberService.create(request);
        });

        assertEquals(USER_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    @DisplayName("✅ 회원 프로필 수정 성공")
    void updateMemberProfile_Success() {
        // Given
        MemberSaveRequest request = MemberSaveRequest.builder()
                .email("test@example.com")
                .password("password123")
                .tel("01012345678")
                .nickname("testUser")
                .build();
        MemberResponse response = memberService.create(request);
        MemberUpdateRequest updateRequest = MemberUpdateRequest.builder()
                .email("test@example.com")
                .password("newPasd123")
                .nickname("newNick")
                .tel("01099999999")
                .build();

        // When
        MemberResponse memberResponse = memberService.updateMemberProfile(updateRequest);

        // Then
        assertNotNull(memberResponse);
        assertEquals(updateRequest.getNickname(), memberResponse.getNickname());
        assertEquals(updateRequest.getTel(), memberResponse.getTel());
    }

    @Test
    @DisplayName("✅ 회원 탈퇴 성공")
    void deleteMember_Success() {
        // Given
        MemberDeleteRequest deleteRequest = MemberDeleteRequest.builder()
                .requestEmail("test@example.com")
                .password("password123")
                .build();
        
        // When
        memberService.deleteMember(deleteRequest);

        // Then
        assertEquals(MemberStatus.INACTIVE, member.getStatus());
    }

    @Test
    @DisplayName("❌ 잘못된 비밀번호 입력 시 예외 발생")
    void deleteMember_Fail_WrongPassword() {
        // Given
        MemberDeleteRequest deleteRequest = new MemberDeleteRequest("test@example.com", "wrongPassword");

        when(securityReadService.getMember()).thenReturn(member);
        when(passwordEncoder.matches(deleteRequest.getPassword(), member.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(PlayHiveException.class, () -> memberService.deleteMember(deleteRequest));
    }
}