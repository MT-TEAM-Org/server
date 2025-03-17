package org.myteam.server.news.newsReplyMember;

import static org.assertj.core.api.Assertions.*;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.domain.Category;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsReply.domain.NewsReply;
import org.myteam.server.news.newsReplyMember.domain.NewsReplyMember;
import org.myteam.server.news.newsReplyMember.service.NewsReplyMemberService;
import org.springframework.beans.factory.annotation.Autowired;

public class NewsReplyMemberServiceTest extends IntegrationTestSupport {

	@Autowired
	private NewsReplyMemberService newsReplyMemberService;

	@DisplayName("사용자 댓글 추천 데이터를 추가한다.")
	@Test
	void saveTest() {
		News news = createNews(1, Category.BASEBALL, 10);
		Member member = createMember(1);

		NewsComment newsComment = createNewsComment(news, member, "뉴스 댓글 테스트1", 10);
		NewsReply newsReply = createNewsReply(newsComment, member, "뉴스 대댓글 테스트1");

		newsReplyMemberService.save(newsReply.getId());

		assertThat(
			newsReplyMemberRepository.findByNewsReplyIdAndMemberPublicId(newsReply.getId(), member.getPublicId())
				.get())
			.extracting("newsReply.id", "member.publicId")
			.contains(newsReply.getId(), member.getPublicId());
	}

	@DisplayName("사용자 추천 데이터를 삭제한다.")
	@Test
	void deleteByNewsIdMemberIdTest() {
		News news = createNews(1, Category.FOOTBALL, 10);
		Member member = createMember(1);
		NewsComment newsComment = createNewsComment(news, member, "뉴스 댓글 테스트1", 10);
		NewsReply newsReply = createNewsReply(newsComment, member, "뉴스 대댓글 테스트1");

		NewsReplyMember newsCommentMember = createNewsReplyMember(member, newsReply);

		newsReplyMemberService.deleteByNewsReplyIdMemberId(newsReply.getId());

		assertThatThrownBy(() -> newsReplyMemberRepository.findById(newsCommentMember.getId()).get())
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage("No value present");
	}
}
