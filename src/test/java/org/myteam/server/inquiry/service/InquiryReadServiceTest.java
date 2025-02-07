package org.myteam.server.inquiry.service;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.inquiry.domain.InquiryOrderType;
import org.myteam.server.inquiry.dto.request.InquiryFindRequest;
import org.myteam.server.inquiry.dto.response.InquiryResponse;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;

class InquiryReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberJpaRepository memberRepository;
    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private InquiryWriteService inquiryWriteService;
    @Autowired
    private InquiryReadService inquiryReadService;

    private Member testMember;
    private Member otherMember;

    private UUID testMemberPublicId;
    private UUID otherMemberPublicId;

    @BeforeEach
    void setUp() {
        testMemberPublicId = memberService.create(MemberSaveRequest.builder()
                        .email("test@example.com")
                        .tel("01012345678")
                        .nickname("testUser")
                        .password("teamPlayHive12#")
                .build()).getPublicId();
        otherMemberPublicId = memberService.create(MemberSaveRequest.builder()
                .email("other@example.com")
                .tel("01087654321")
                .nickname("otherUser")
                .password("otherMember!@#")
                .build()).getPublicId();
    }

    @AfterEach
    void cleanUp() {
        inquiryRepository.deleteAllInBatch();
        memberJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("회원의 문의 내역을 정상적으로 조회한다.")
    void shouldReturnPagedInquiriesForMember() {
        // Given
        testMember = memberRepository.findByPublicId(testMemberPublicId).get();
        for (int i = 1; i <= 15; i++) {
            inquiryWriteService.createInquiry("문의내역 " + i, testMemberPublicId, "127.0.0.1");
        }
        otherMember = memberRepository.findByPublicId(otherMemberPublicId).get();
        for (int i = 1; i <= 15; i++) {
            inquiryWriteService.createInquiry("건의사항 " + i, otherMemberPublicId, "127.0.0.1");
        }

        // When
        PageCustomResponse<InquiryResponse> response = inquiryReadService.getInquiriesByMember(new InquiryFindRequest(testMember.getPublicId(), InquiryOrderType.RECENT, 2, 5));

        // Then
        System.out.println(response);
        assertThat("문의내역 6").isEqualTo(response.getContent().get(0).getContent());
        assertThat(response.getContent()).hasSize(5);
        assertThat(response.getPageInfo().getCurrentPage()).isEqualTo(2);
        assertThat(response.getPageInfo().getTotalPage()).isEqualTo(3);
        assertThat(response.getPageInfo().getTotalElement()).isEqualTo(15);
    }
}
