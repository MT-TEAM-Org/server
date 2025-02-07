package org.myteam.server.news.newsComment.service;

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
import org.myteam.server.news.newsComment.dto.repository.NewsCommentDto;
import org.myteam.server.news.newsComment.dto.service.request.NewsCommentServiceRequest;
import org.myteam.server.news.newsComment.dto.service.response.NewsCommentListResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class NewsCommentReadServiceTest extends IntegrationTestSupport {

	@Autowired
	private NewsCommentReadService newsCommentReadService;

	@DisplayName("뉴스 댓글 ID로 뉴스댓글을 조회한다.")
	@Test
	void findByIdTest() {
		News news = createNews(1, NewsCategory.BASEBALL, 10);
		Member member = createMember(1);

		NewsComment newsComment = craeteNewsComment(news, member, "뉴스 댓글 테스트");

		NewsComment findNewsComment = newsCommentReadService.findById(newsComment.getId());

		assertThat(findNewsComment)
			.extracting("id", "news.id", "member.publicId", "comment")
			.contains(newsComment.getId(), news.getId(), member.getPublicId(), "뉴스 댓글 테스트");
	}

	@DisplayName("뉴스 댓글 ID로 조회시 조회하지 않으면 예외가 발생한다.")
	@Test
	void findByIdNotExistThrowExceptionTest() {
		assertThatThrownBy(() -> newsCommentReadService.findById(1L))
			.isInstanceOf(PlayHiveException.class)
			.hasMessage(ErrorCode.NEWS_COMMENT_NOT_FOUND.getMsg());
	}

	@DisplayName("뉴스ID로 댓글리스트를 조회한다.")
	@Test
	void findByNewsIdTest() {
		News news = createNews(1, NewsCategory.BASEBALL, 10);
		Member member1 = createMember(1);
		Member member2 = createMember(1);

		NewsComment newsComment1 = craeteNewsComment(news, member1, "뉴스 댓글 테스트1");
		NewsComment newsComment2 = craeteNewsComment(news, member2, "뉴스 댓글 테스트2");

		NewsCommentServiceRequest newsCommentServiceRequest = NewsCommentServiceRequest.builder()
			.newsId(news.getId())
			.page(1)
			.size(10)
			.build();

		NewsCommentListResponse newsCommentListResponse = newsCommentReadService.findByNewsId(
			newsCommentServiceRequest);

		List<NewsCommentDto> newsCommentList = newsCommentListResponse.getList().getContent();
		PageableCustomResponse pageInfo = newsCommentListResponse.getList().getPageInfo();

		assertAll(
			() -> assertThat(pageInfo)
				.extracting("currentPage", "totalPage", "totalElement")
				.containsExactlyInAnyOrder(
					1, 1, 2L
				),
			() -> assertThat(newsCommentList)
				.extracting("newsCommentId", "newsId", "memberDto.publicId", "memberDto.nickName", "comment")
				.contains(
					Tuple.tuple(newsComment1.getId(), newsComment1.getNews().getId(), newsComment1.getMember().getPublicId(),
						"test",
						"뉴스 댓글 테스트1"),
					Tuple.tuple(newsComment2.getId(), newsComment2.getNews().getId(), newsComment2.getMember().getPublicId(),
						"test",
						"뉴스 댓글 테스트2")
				)
		);
	}
}
