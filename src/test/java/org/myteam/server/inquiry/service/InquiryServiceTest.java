package org.myteam.server.inquiry.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;
import java.util.UUID;

class InquiryServiceTest extends IntegrationTestSupport {
    private Member testMember;
    private UUID memberPublicId;

    @BeforeEach
    void setUp() {
        // 가짜 MemberResponse 객체 생성
        Member member = Member.builder()
                .publicId(UUID.randomUUID())
                .email("test@example.com")
                .encodedPassword("teamPlayHive12#")
                .tel("01012345678")
                .nickname("testUser")
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .status(MemberStatus.ACTIVE)
                .build();

        // 테스트용 Member 저장 (실제 DB에 넣기)
        testMember = memberJpaRepository.save(member);
        memberPublicId = testMember.getPublicId();
    }

    @Test
    @DisplayName("문의가 정상적으로 생성된다.")
    void shouldCreateInquirySuccessfully() {
        // Given
        // When
        String content = inquiryService.createInquiry("문의내역", memberPublicId, "127.0.0.1");

        // Then
        assertThat(content).isNotNull();
        assertThat(content).isEqualTo("문의내역");
    }
}
