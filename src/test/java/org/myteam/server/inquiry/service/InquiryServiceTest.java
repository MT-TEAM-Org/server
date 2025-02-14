package org.myteam.server.inquiry.service;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;

class InquiryServiceTest extends IntegrationTestSupport {
	private Member testMember;
	private UUID memberPublicId;

	@BeforeEach
	void setUp() {
		// 가짜 MemberResponse 객체 생성
		Member member = Member.builder()
			.publicId(UUID.randomUUID())
			.email("test@example.com")
			.password("teamPlayHive12#")
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
