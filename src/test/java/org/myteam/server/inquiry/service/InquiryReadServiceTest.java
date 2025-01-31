package org.myteam.server.inquiry.service;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;

@SpringBootTest
class InquiryReadServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private InquiryWriteService inquiryWriteService;
    @Autowired
    private InquiryReadService inquiryReadService;

    private Member testMember;

    private List<Inquiry> inquiries;
    private UUID memberPublicId;

    @BeforeEach
    void setUp() {
        memberPublicId = memberService.create(MemberSaveRequest.builder()
                        .email("test@example.com")
                        .tel("01012345678")
                        .nickname("testUser")
                        .password("teamPlayHive12#")
                .build()).getPublicId();
        System.out.println("memberPublicId = " + memberPublicId);
    }

    @Test
    @DisplayName("회원의 문의 내역을 정상적으로 조회한다.")
    void shouldReturnPagedInquiriesForMember() {
        // Given
        testMember = memberRepository.findByPublicId(memberPublicId).get();
        System.out.println(testMember.getPublicId());
        inquiryWriteService.createInquiry("문의내역", memberPublicId, "127.0.0.1");
        inquiries = List.of(new Inquiry(1L, "문의내역", testMember, "127.0.0.1", LocalDateTime.now()));

        // When
        PageCustomResponse<Inquiry> response = inquiryReadService.getInquiriesByMember(testMember.getPublicId(), new PageInfoRequest(1, 10));

        // Then
        assertThat("문의내역").isEqualTo(response.getContent().get(0).getContent());
        assertThat(response.getContent()).hasSize(1);
    }
}