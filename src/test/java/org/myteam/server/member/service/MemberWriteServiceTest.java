package org.myteam.server.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.myteam.server.global.exception.ErrorCode.USER_ALREADY_EXISTS;


class MemberWriteServiceTest  extends IntegrationTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private MemberWriteService memberWriteService;

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
        MemberResponse response = memberWriteService.create(request);

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
        MemberResponse response = memberWriteService.create(request);

        // When & Then
        PlayHiveException exception = assertThrows(PlayHiveException.class, () -> {
            memberWriteService.create(request);
        });

        assertEquals(USER_ALREADY_EXISTS, exception.getErrorCode());
    }
}