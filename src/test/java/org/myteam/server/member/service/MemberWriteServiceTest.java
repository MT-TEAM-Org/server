package org.myteam.server.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.myteam.server.global.exception.ErrorCode.USER_ALREADY_EXISTS;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.common.certification.mail.core.MailStrategy;
import org.myteam.server.common.certification.mail.domain.EmailType;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.profile.dto.request.ProfileRequestDto.MemberUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


class MemberWriteServiceTest extends IntegrationTestSupport {

    @Autowired
    protected MemberService memberService;

    @Test
    @DisplayName("✅ 회원 가입 성공")
    @Transactional
    void createMember_Success() {
        // Given
        MailStrategy mockStrategy = mock(MailStrategy.class);
        when(mailStrategyFactory.getStrategy(EmailType.WELCOME)).thenReturn(mockStrategy);
        doNothing().when(mockStrategy).send(anyString());

        MemberSaveRequest request = MemberSaveRequest.builder()
                .email("test@example.com")
                .password("password123")
                .tel("01012345678")
                .nickname("testUser")
                .build();

        // When
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
        MailStrategy mockStrategy = mock(MailStrategy.class);
        when(mailStrategyFactory.getStrategy(EmailType.WELCOME)).thenReturn(mockStrategy);
        doNothing().when(mockStrategy).send(anyString());

        MemberSaveRequest request = MemberSaveRequest.builder()
                .email("test@example.com")
                .password("password123")
                .tel("01012345678")
                .nickname("testUser")
                .build();
        memberService.create(request);

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
        MailStrategy mockStrategy = mock(MailStrategy.class);
        when(mailStrategyFactory.getStrategy(EmailType.WELCOME)).thenReturn(mockStrategy);
        doNothing().when(mockStrategy).send(anyString());

        MemberSaveRequest request = MemberSaveRequest.builder()
                .email("test@example.com")
                .password("password123")
                .tel("01012345678")
                .nickname("testUser")
                .build();
        MemberResponse memberResponse = memberService.create(request);
        Member member = memberJpaRepository.findByPublicId(memberResponse.getPublicId()).get();

        MemberUpdateRequest updateRequest = MemberUpdateRequest.builder()
                .email("test@example.com")
                .password("newPasd123")
                .nickname("newNick")
                .tel("01099999999")
                .build();

        Member mockMember = Member.builder()
                .publicId(UUID.randomUUID())
                .email(request.getEmail())
                .nickname("testUser")
                .tel("01012345678")
                .status(MemberStatus.ACTIVE)
                .build();

        when(securityReadService.getMember()).thenReturn(member);

        // When
        MemberResponse memberUpdateResponse = memberService.updateMemberProfile(updateRequest);

        // Then
        assertNotNull(memberUpdateResponse);
        assertEquals(updateRequest.getNickname(), memberUpdateResponse.getNickname());
        assertEquals(updateRequest.getTel(), memberUpdateResponse.getTel());
    }

    @Test
    @DisplayName("✅ 회원 탈퇴 성공")
    void deleteMember_Success() {
        // Given
        MailStrategy mockStrategy = mock(MailStrategy.class);
        when(mailStrategyFactory.getStrategy(EmailType.WELCOME)).thenReturn(mockStrategy);
        doNothing().when(mockStrategy).send(anyString());

        MemberSaveRequest request = MemberSaveRequest.builder()
                .email("test@example.com")
                .password("password123")
                .tel("01012345678")
                .nickname("testUser")
                .build();
        MemberResponse memberResponse = memberService.create(request);
        Member member = memberJpaRepository.findByPublicId(memberResponse.getPublicId()).get();

        // When
        when(securityReadService.getMember()).thenReturn(member);
        doNothing().when(redisService).deleteRefreshToken(any());
        memberService.deleteMember();

        // Then
        assertEquals(MemberStatus.INACTIVE, member.getStatus());
    }
}