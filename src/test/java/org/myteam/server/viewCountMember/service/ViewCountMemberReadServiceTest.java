package org.myteam.server.viewCountMember.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.member.entity.Member;
import org.myteam.server.viewCountMember.domain.ViewCountMember;
import org.myteam.server.viewCountMember.domain.ViewType;
import org.springframework.beans.factory.annotation.Autowired;

public class ViewCountMemberReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private ViewCountMemberReadService viewCountMemberReadService;

	@DisplayName("로그인한 사용자가 조회한 계시물인지 조회한다..")
	@Test
	void existsByViewIdAndMemberPublicId() {
		Member member = createMember(1);
		ViewCountMember viewCountMember = createViewCountMember(1L, member, ViewType.BOARD);

		assertThat(viewCountMemberReadService.existsByViewIdAndMemberPublicId(1L, member.getPublicId())).isTrue();
	}
}
