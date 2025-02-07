package org.myteam.server.news.newsComment.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsComment.dto.service.request.NewsCommentSaveServiceRequest;
import org.myteam.server.news.newsComment.dto.service.request.NewsCommentUpdateServiceRequest;
import org.myteam.server.news.newsComment.dto.service.response.NewsCommentResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class NewsCommentServiceTest extends IntegrationTestSupport {

	@Autowired
	private NewsCommentService newsCommentService;

	@DisplayName("뉴스댓글을 저장한다.")
	@Test
	void saveTest() {
		News news = createNews(1, NewsCategory.BASEBALL, 10);
		Member member = createMember(1);

		NewsCommentSaveServiceRequest newsCommentSaveServiceRequest = NewsCommentSaveServiceRequest.builder()
			.newsId(news.getId())
			.comment("댓글 테스트")
			.ip("1.1.1.1")
			.build();

		NewsCommentResponse newsCommentResponse = newsCommentService.save(newsCommentSaveServiceRequest);

		assertAll(
			() -> assertThat(newsCommentRepository.findById(newsCommentResponse.getNewsCommentId()).get())
				.extracting("id", "news.id", "member.publicId", "comment", "ip")
				.contains(newsCommentResponse.getNewsCommentId(), news.getId(), member.getPublicId(), "댓글 테스트", "1.1.1.1"),
			() -> assertThat(newsCountRepository.findById(news.getId()).get().getCommentCount()).isEqualTo(11)
		);
	}

	@DisplayName("뉴스댓글을 수정한다.")
	@Test
	void updateTest() {
		News news = createNews(1, NewsCategory.BASEBALL, 10);
		Member member = createMember(1);

		NewsComment newsComment = craeteNewsComment(news, member, "뉴스 댓글 테스트");

		NewsCommentUpdateServiceRequest newsCommentUpdateServiceRequest = NewsCommentUpdateServiceRequest.builder()
			.newsCommentId(newsComment.getId())
			.comment("뉴스 댓글 수정 테스트")
			.build();

		Long updatedCommentId = newsCommentService.update(newsCommentUpdateServiceRequest);

		assertThat(newsCommentRepository.findById(updatedCommentId).get())
			.extracting("id", "news.id", "member.publicId", "comment", "ip")
			.contains(newsComment.getId(), news.getId(), member.getPublicId(), "뉴스 댓글 수정 테스트", "1.1.1.1");
	}

	@DisplayName("뉴스댓글을 삭제한다.")
	@Test
	void deleteTest() {
		News news = createNews(1, NewsCategory.BASEBALL, 10);
		Member member = createMember(1);

		NewsComment newsComment = craeteNewsComment(news, member, "뉴스 댓글 테스트");
		Long deletedCommentId = newsCommentService.delete(newsComment.getId());

		assertAll(
			() -> assertThatThrownBy(() -> newsCommentService.delete(deletedCommentId))
				.isInstanceOf(PlayHiveException.class)
				.hasMessage(ErrorCode.NEWS_COMMENT_NOT_FOUND.getMsg()),
			() -> assertThat(newsCountRepository.findById(news.getId()).get().getCommentCount()).isEqualTo(9)
		);
	}
}
