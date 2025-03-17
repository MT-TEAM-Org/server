package org.myteam.server.news.newsCommentMember;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsCommentMember.service.NewsCommentMemberReadService;
import org.springframework.beans.factory.annotation.Autowired;

public class NewsCommentMemberReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private NewsCommentMemberReadService newsCommentMemberReadService;

	@DisplayName("이미 좋아요를 누른 뉴스면 예외가 발생한다.")
	@Test
	void confirmExistMemberTest() {
		News news = createNews(1, Category.BASEBALL, 10);
		Member member = createMember(1);

		NewsComment newsComment = createNewsComment(news, member, "뉴스 댓글 테스트1", 10);

		createNewsCommentMember(member, newsComment);

		assertThatThrownBy(() -> newsCommentMemberReadService.confirmExistMember(newsComment.getId(), member.getPublicId()))
			.isInstanceOf(PlayHiveException.class)
			.hasMessage(ErrorCode.ALREADY_MEMBER_RECOMMEND_NEWS_COMMENT.getMsg());

	}

}
