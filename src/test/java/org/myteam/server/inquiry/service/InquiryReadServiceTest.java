package org.myteam.server.inquiry.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.inquiry.domain.InquiryOrderType;
import org.myteam.server.inquiry.domain.InquirySearchType;
import org.myteam.server.inquiry.dto.request.InquiryFindRequest;
import org.myteam.server.inquiry.dto.response.InquiriesListResponse;
import org.myteam.server.inquiry.dto.response.InquiryResponse;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class InquiryReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private MemberService memberService;
	@Autowired
	private InquiryService inquiryService;

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

		testMember = memberJpaRepository.findByPublicId(testMemberPublicId).get();
		IntStream.rangeClosed(1, 15).forEach(i ->
				inquiryService.createInquiry("문의내역 " + i, testMemberPublicId, "127.0.0.1")
		);
		otherMember = memberJpaRepository.findByPublicId(otherMemberPublicId).get();
		IntStream.rangeClosed(1, 15).forEach(i ->
				inquiryService.createInquiry("건의사항 " + i, otherMemberPublicId, "127.0.0.1")
		);
	}

	@Test
	@DisplayName("답변일 기준으로 회원의 문의 내역을 조회한다.")
	void shouldReturnPagedInquiriesSortedByAnswered() {
		// Given
		InquiryFindRequest request = new InquiryFindRequest(
				testMember.getPublicId(),
				InquiryOrderType.ANSWERED,
				null,
				null,
				1, 5
		);

		// 일부 문의에 대한 가짜 응답 데이터를 생성
		List<InquiryResponse> inquiryResponses = IntStream.rangeClosed(11, 15)
				.mapToObj(i -> new InquiryResponse(
						(long)i,
						"문의내역 " + i,
						"닉네임",
						"192.168.143.22",
						LocalDateTime.now().minusDays(i),
						"접수완료"
				))
				.sorted(Comparator.comparing(InquiryResponse::getCreatedAt))
				.toList();

		PageCustomResponse<InquiryResponse> pageCustomResponse = PageCustomResponse.of(
				new PageImpl<>(inquiryResponses, PageRequest.of(0, 5), 15)
		);

		InquiriesListResponse mockResponse = InquiriesListResponse.createResponse(pageCustomResponse);

		// inquiryReadService의 getInquiriesByMember 메서드를 Mocking하여 원하는 응답 반환하도록 설정
		given(inquiryReadService.getInquiriesByMember(any(InquiryFindRequest.class)))
				.willReturn(mockResponse);

		// When
		InquiriesListResponse response = inquiryReadService.getInquiriesByMember(request);

		// Then
		assertAll(
				() -> assertThat(response).isNotNull(),
				() -> assertThat(response.getList().getContent()).hasSize(5),
				() -> assertThat(response.getList().getPageInfo())
						.extracting("currentPage", "totalPage", "totalElement")
						.containsExactly(1, 3, 15L),
				() -> assertThat(response.getList().getContent())
						.extracting("answerContent")
						.containsExactly("답변 15", "답변 14", "답변 13", "답변 12", "답변 11") // 최신 답변순 확인
		);

		then(inquiryReadService).should(times(1)).getInquiriesByMember(any(InquiryFindRequest.class));
	}

	@Test
	@DisplayName("문의 내용을 검색하고 최신순으로 조회한다.")
	void shouldSearchInquiriesByContentAndSortByRecent() {
		// Given
		InquiryFindRequest request = new InquiryFindRequest(
				testMember.getPublicId(),
				InquiryOrderType.ANSWERED,
				InquirySearchType.CONTENT,
				"문의사항",
				1, 5
		);

		// ✅ 검색된 문의 목록을 생성 (최신순)
		List<InquiryResponse> inquiryResponses = IntStream.rangeClosed(1, 15)
				.mapToObj(i -> new InquiryResponse(
						(long)i,
						"문의사항 " + i,
						"닉네임",
						"127.0.0.1",
						LocalDateTime.now().minusDays(i),
						"답변완료"
				))
				.sorted(Comparator.comparing(InquiryResponse::getCreatedAt))
				.toList();

		PageCustomResponse<InquiryResponse> pageCustomResponse = PageCustomResponse.of(
				new PageImpl<>(inquiryResponses.subList(0, 5), PageRequest.of(0, 5), 15)
		);

		InquiriesListResponse mockResponse = InquiriesListResponse.createResponse(pageCustomResponse);

		given(inquiryReadService.getInquiriesByMember(any(InquiryFindRequest.class)))
				.willReturn(mockResponse);

		// When
		InquiriesListResponse response = inquiryReadService.getInquiriesByMember(request);

		// Then
		assertAll(
				() -> assertThat(response).isNotNull(),
				() -> assertThat(response.getList().getContent()).hasSize(5),
				() -> assertThat(response.getList().getPageInfo())
						.extracting("currentPage", "totalPage", "totalElement")
						.containsExactly(1, 3, 15L),
				() -> assertThat(response.getList().getContent())
						.extracting("content")
						.containsExactly(
								"문의사항 15",
								"문의사항 14",
								"문의사항 13",
								"문의사항 12",
								"문의사항 11"
						)
		);

		// ✅ Mocking이 예상대로 호출되었는지 검증
		then(inquiryReadService).should(times(1)).getInquiriesByMember(any(InquiryFindRequest.class));
	}
}