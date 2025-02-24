package org.myteam.server.news.newsCommentMember;

import static org.assertj.core.api.Assertions.*;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsCommentMember.domain.NewsCommentMember;
import org.myteam.server.news.newsCommentMember.dto.service.response.NewsCommentMemberResponse;
import org.myteam.server.news.newsCommentMember.service.NewsCommentMemberService;
import org.springframework.beans.factory.annotation.Autowired;

public class NewsCommentMemberServiceTest extends IntegrationTestSupport {

	@Autowired
	private NewsCommentMemberService newsCommentMemberService;

	@DisplayName("사용자 댓글 추천 데이터를 추가한다.")
	@Test
	void saveTest() {
		News news = createNews(1, NewsCategory.BASEBALL, 10);
		Member member = createMember(1);

		NewsComment newsComment = createNewsComment(news, member, "뉴스 댓글 테스트1");

		NewsCommentMemberResponse newsCommentMemberResponse = newsCommentMemberService.save(newsComment.getId());

		assertThat(
			newsCommentMemberRepository.findByNewsCommentIdAndMemberPublicId(newsComment.getId(), member.getPublicId())
				.get())
			.extracting("id", "member.publicId")
			.contains(newsCommentMemberResponse.getNewsCommentMemberId(), member.getPublicId());
	}

	@DisplayName("사용자 추천 데이터를 삭제한다.")
	@Test
	void deleteByNewsIdMemberIdTest() {
		News news = createNews(1, NewsCategory.FOOTBALL, 10);
		Member member = createMember(1);
		NewsComment newsComment = createNewsComment(news, member, "뉴스 댓글 테스트1");

		NewsCommentMember newsCommentMember = createNewsCommentMember(member, newsComment);

		newsCommentMemberService.deleteByNewsCommentIdMemberId(newsComment.getId());

		assertThatThrownBy(() -> newsCommentMemberRepository.findById(newsCommentMember.getId()).get())
			.isInstanceOf(NoSuchElementException.class)
			.hasMessage("No value present");
	}
}
