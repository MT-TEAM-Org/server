package org.myteam.server.news.newsReply.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageableCustomResponse;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsReply.domain.NewsReply;
import org.myteam.server.news.newsReply.dto.repository.NewsReplyDto;
import org.myteam.server.news.newsReply.dto.service.request.NewsReplyServiceRequest;
import org.myteam.server.news.newsReply.dto.service.response.NewsReplyListResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class NewsReplyReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private NewsReplyReadService newsReplyReadService;

	@DisplayName("뉴스 대댓글 ID로 뉴스 대댓글을 조회한다.")
	@Test
	void findByIdTest() {
		News news = createNews(1, NewsCategory.BASEBALL, 10);
		Member member = createMember(1);

		NewsComment newsComment = createNewsComment(news, member, "뉴스 댓글 테스트");

		NewsReply newsReply = createNewsReply(newsComment, member, "뉴스 대댓글 테스트");

		NewsReply findNewsReply = newsReplyReadService.findById(newsReply.getId());

		assertThat(findNewsReply)
			.extracting("id", "newsComment.id", "member.publicId", "comment")
			.contains(newsReply.getId(), newsComment.getId(), member.getPublicId(), "뉴스 대댓글 테스트");
	}

	@DisplayName("뉴스 댓글 ID로 조회시 존재하지 않으면 예외가 발생한다.")
	@Test
	void findByIdNotExistThrowExceptionTest() {
		assertThatThrownBy(() -> newsReplyReadService.findById(1L))
			.isInstanceOf(PlayHiveException.class)
			.hasMessage(ErrorCode.NEWS_REPLY_NOT_FOUND.getMsg());
	}

	@DisplayName("댓글ID로 대댓글리스트를 조회한다.")
	@Test
	void findByNewsIdTest() {
		News news = createNews(1, NewsCategory.BASEBALL, 10);
		Member member1 = createMember(1);
		Member member2 = createMember(1);

		NewsComment newsComment1 = createNewsComment(news, member1, "뉴스 댓글 테스트1");

		NewsReply newsReply1 = createNewsReply(newsComment1, member1, "뉴스 대댓글 테스트1");
		NewsReply newsReply2 = createNewsReply(newsComment1, member2, "뉴스 대댓글 테스트2");
		NewsReply newsReply3 = createNewsReply(newsComment1, member2, "뉴스 대댓글 테스트3");

		NewsReplyServiceRequest newsReplyServiceRequest = NewsReplyServiceRequest.builder()
			.newsCommentId(newsComment1.getId())
			.page(1)
			.size(2)
			.build();

		NewsReplyListResponse newsReplyListResponse = newsReplyReadService.findByNewsCommentId(
			newsReplyServiceRequest);

		List<NewsReplyDto> newsReplyList = newsReplyListResponse.getList().getContent();
		PageableCustomResponse pageInfo = newsReplyListResponse.getList().getPageInfo();

		assertAll(
			() -> assertThat(pageInfo)
				.extracting("currentPage", "totalPage", "totalElement")
				.containsExactlyInAnyOrder(
					1, 2, 3L
				),
			() -> assertThat(newsReplyList)
				.extracting("newsReplyId", "newsCommentId", "member.publicId", "member.nickName", "comment")
				.contains(
					Tuple.tuple(newsReply1.getId(), newsReply1.getNewsComment().getId(), newsReply1.getMember().getPublicId(),
						"test",
						"뉴스 대댓글 테스트1"),
					Tuple.tuple(newsReply2.getId(), newsReply2.getNewsComment().getId(), newsReply2.getMember().getPublicId(),
						"test",
						"뉴스 대댓글 테스트2")
				)
		);
	}
}
