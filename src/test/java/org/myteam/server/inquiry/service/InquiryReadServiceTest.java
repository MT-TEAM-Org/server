package org.myteam.server.inquiry.service;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.inquiry.domain.InquiryOrderType;
import org.myteam.server.inquiry.domain.InquirySearchType;
import org.myteam.server.inquiry.dto.request.InquirySearchRequest;
import org.myteam.server.inquiry.dto.request.InquiryFindRequest;
import org.myteam.server.inquiry.dto.response.InquiryResponse;
import org.myteam.server.inquiry.repository.InquiryAnswerRepository;
import org.myteam.server.inquiry.repository.InquiryRepository;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.MemberWriteService;
import org.springframework.beans.factory.annotation.Autowired;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;

=======
>>>>>>> e72759b (feat: 회원 정보 수정 및 삭제)
import java.util.UUID;
import java.util.stream.IntStream;

class InquiryReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private MemberWriteService memberService;
    @Autowired
    private MemberJpaRepository memberRepository;
    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private InquiryWriteService inquiryWriteService;
    @Autowired
    private InquiryReadService inquiryReadService;

    @Autowired
    private InquiryAnswerWriteService inquiryAnswerWriteService;
    @Autowired
    private InquiryAnswerRepository inquiryAnswerRepository;

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

        testMember = memberRepository.findByPublicId(testMemberPublicId).get();
        IntStream.rangeClosed(1, 15).forEach(i ->
                inquiryWriteService.createInquiry("문의내역 " + i, testMemberPublicId, "127.0.0.1")
        );
        otherMember = memberRepository.findByPublicId(otherMemberPublicId).get();
        IntStream.rangeClosed(1, 15).forEach(i ->
                inquiryWriteService.createInquiry("건의사항 " + i, otherMemberPublicId, "127.0.0.1")
        );
    }

    @AfterEach
    void cleanUp() {
        inquiryAnswerRepository.deleteAllInBatch();
        inquiryRepository.deleteAllInBatch();
        memberJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("최신순으로 회원의 문의 내역을 조회한다.")
    void shouldReturnPagedInquiriesForMember() {
        // Given

        // When
        PageCustomResponse<InquiryResponse> response = inquiryReadService.getInquiriesByMember(
                new InquirySearchRequest(
                        testMember.getPublicId(),
                        InquiryOrderType.RECENT,
                        null,
                        null,
                        2,
                        5));

        // Then
        System.out.println("response: " + response);
        assertThat("문의내역 10").isEqualTo(response.getContent().get(0).getContent());
        assertThat(response.getContent()).hasSize(5);
        assertThat(response.getPageInfo().getCurrentPage()).isEqualTo(2);
        assertThat(response.getPageInfo().getTotalPage()).isEqualTo(3);
        assertThat(response.getPageInfo().getTotalElement()).isEqualTo(15);
    }

    @Test
    @DisplayName("답변일 기준으로 회원의 문의 내역을 조회한다.")
    void shouldReturnPagedInquiriesSortedByAnswered() {
        // Given

        // 일부 문의에 답변 추가
        for (int i = 1; i <= 5; i++) {
            inquiryAnswerWriteService.createAnswer(Long.valueOf(i), "답변 " + i);
        }

        // When
        PageCustomResponse<InquiryResponse> response = inquiryReadService.getInquiriesByMember(
                new InquirySearchRequest(
                        testMember.getPublicId(),
                        InquiryOrderType.ANSWERED,
                        null,
                        null,
                        1, 5));

        // Then
        System.out.println("response: " + response);
        assertThat(response.getContent().get(0).getAnswerContent()).isEqualTo("답변 5");
        assertThat(response.getContent()).hasSize(5);
        assertThat(response.getPageInfo().getCurrentPage()).isEqualTo(1);
        assertThat(response.getPageInfo().getTotalPage()).isEqualTo(3);
        assertThat(response.getPageInfo().getTotalElement()).isEqualTo(15);
    }

    @Test
    @DisplayName("문의 내용을 검색하고 최신순으로 조회한다.")
    void shouldSearchInquiriesByContentAndSortByRecent() {
        // Given
        for (int i = 1; i <= 15; i++) {
            inquiryWriteService.createInquiry("검색어 포함 " + i, testMemberPublicId, "127.0.0.1");
        }

        // When
        PageCustomResponse<InquiryResponse> response = inquiryReadService.getInquiriesByMember(
                new InquirySearchRequest(
                        testMember.getPublicId(),
                        InquiryOrderType.RECENT,
                        InquirySearchType.CONTENT,
                        "검색어", // ✅ 검색어 적용
                        1, 5));

        // Then
        System.out.println("response: " + response);
        assertThat(response.getContent().get(0).getContent()).contains("검색어 포함 15"); // ✅ 최신순 확인
        assertThat(response.getContent()).hasSize(5);
        assertThat(response.getPageInfo().getCurrentPage()).isEqualTo(1);
        assertThat(response.getPageInfo().getTotalPage()).isEqualTo(3);
    }
}
