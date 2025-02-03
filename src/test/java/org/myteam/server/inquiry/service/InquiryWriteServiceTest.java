package org.myteam.server.inquiry.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.UUID;

@SpringBootTest
class InquiryWriteServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private InquiryWriteService inquiryWriteService;
    @Autowired
    private InquiryRepository inquiryRepository;

    private Member testMember;
    private UUID memberPublicId;

    @BeforeEach
    void setUp() {
        memberPublicId = memberService.create(MemberSaveRequest.builder()
                .email("test@example.com")
                .tel("01012345678")
                .nickname("testUser")
                .password("teamPlayHive12#")
                .build()).getPublicId();
    }

    @AfterEach
    void cleanUp() {
        inquiryRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("문의가 정상적으로 생성된다.")
    void shouldCreateInquirySuccessfully() {
        // Given
        testMember = memberRepository.findByPublicId(memberPublicId).get();
        System.out.println(testMember.getPublicId());

        // When
        inquiryWriteService.createInquiry("문의내역", memberPublicId, "127.0.0.1");
        List<Inquiry> inquiries = inquiryRepository.findAll();

        // Then
        assertThat(inquiries).isNotEmpty();
        assertThat(inquiries.get(0).getContent()).isEqualTo("문의내역");
    }
}
