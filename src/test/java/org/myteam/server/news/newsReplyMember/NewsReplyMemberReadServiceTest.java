package org.myteam.server.news.newsReplyMember;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsCommentMember.service.NewsCommentMemberReadService;
import org.myteam.server.news.newsReply.domain.NewsReply;
import org.myteam.server.news.newsReplyMember.service.NewsReplyMemberReadService;
import org.springframework.beans.factory.annotation.Autowired;

public class NewsReplyMemberReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private NewsReplyMemberReadService newsReplyMemberReadService;

	@DisplayName("이미 좋아요를 누른 뉴스 대댓글이면 예외가 발생한다.")
	@Test
	void confirmExistMemberTest() {
		News news = createNews(1, NewsCategory.BASEBALL, 10);
		Member member = createMember(1);

		NewsComment newsComment = createNewsComment(news, member, "뉴스 댓글 테스트1", 10);
		NewsReply newsReply = createNewsReply(newsComment, member, "뉴스 대댓글 테스트1");

		createNewsReplyMember(member, newsReply);

		assertThatThrownBy(() -> newsReplyMemberReadService.confirmExistMember(newsReply.getId(), member.getPublicId()))
			.isInstanceOf(PlayHiveException.class)
			.hasMessage(ErrorCode.ALREADY_MEMBER_RECOMMEND_NEWS_REPLY.getMsg());

	}

}
