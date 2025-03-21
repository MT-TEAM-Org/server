package org.myteam.server.viewCountMember.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.member.entity.Member;
import org.myteam.server.viewCountMember.domain.ViewType;
import org.springframework.beans.factory.annotation.Autowired;

public class ViewCountServiceTest extends IntegrationTestSupport {

	@Autowired
	private ViewCountService viewCountService;

	@DisplayName("publicId가 있는 경우 저장이 되는지 테스트한다.")
	@Test
	void confirmPostViewTest() {
		Member member = createMember(1);

		boolean isView = viewCountService.confirmPostView(null, null, ViewType.NEWS, 1L, member.getPublicId());

		assertAll(
			() -> assertThat(isView).isFalse(),
			() -> assertThat(viewCountMemberRepository.findAll())
				.extracting(
					"viewId", "member.publicId", "viewType")
				.containsExactly(
					tuple(
						1L, member.getPublicId(), ViewType.NEWS
					)
				)
		);
	}
}
