package org.myteam.server.news.newsCountMember;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsCountMember.service.NewsCountMemberReadService;
import org.springframework.beans.factory.annotation.Autowired;

public class NewsCountMemberReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private NewsCountMemberReadService newsCountMemberReadService;

	@DisplayName("이미 좋아요를 누른 뉴스면 예외가 발생한다.")
	@Test
	void confirmExistMemberTest() {
		News news = createNews(1, Category.FOOTBALL, 10);
		Member member = createMember(1);

		createNewsCountMember(member, news);

		assertThatThrownBy(() -> newsCountMemberReadService.confirmExistMember(news.getId(), member.getPublicId()))
			.isInstanceOf(PlayHiveException.class)
			.hasMessage(ErrorCode.ALREADY_MEMBER_RECOMMEND_NEWS.getMsg());

	}

}
