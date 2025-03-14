package org.myteam.server.news.newsReply.service;

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
import org.myteam.server.news.newsReply.domain.NewsReply;
import org.myteam.server.news.newsReply.dto.service.request.NewsReplySaveServiceRequest;
import org.myteam.server.news.newsReply.dto.service.request.NewsReplyUpdateServiceRequest;
import org.myteam.server.news.newsReply.dto.service.response.NewsReplyResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class NewsReplyServiceTest extends IntegrationTestSupport {

	@Autowired
	private NewsReplyService newsReplyService;

	@DisplayName("뉴스 대댓글을 저장한다.")
	@Test
	void saveTest() {
		News news = createNews(1, NewsCategory.BASEBALL, 10);
		Member member = createMember(1);
		NewsComment newsComment = createNewsComment(news, member, "뉴스 댓글 테스트1", 10);

		NewsReplySaveServiceRequest newsReplySaveServiceRequest = NewsReplySaveServiceRequest.builder()
			.newsCommentId(newsComment.getId())
			.comment("대댓글 테스트")
			.ip("1.1.1.1")
			.imgUrl("www.test.com")
			.build();

		NewsReplyResponse newsReplyResponse = newsReplyService.save(newsReplySaveServiceRequest);

		assertAll(
			() -> assertThat(newsReplyRepository.findById(newsReplyResponse.getNewsReplyId()).get())
				.extracting("id", "newsComment.id", "member.publicId", "comment", "ip", "imgUrl")
				.contains(newsReplyResponse.getNewsReplyId(), newsComment.getId(), member.getPublicId(), "대댓글 테스트",
					"1.1.1.1", "www.test.com"),
			() -> assertThat(newsCountRepository.findByNewsId(news.getId()).get().getCommentCount()).isEqualTo(11)
		);
	}

	@DisplayName("뉴스 대댓글을 수정한다.")
	@Test
	void updateTest() {
		News news = createNews(1, NewsCategory.BASEBALL, 10);
		Member member = createMember(1);
		NewsComment newsComment = createNewsComment(news, member, "뉴스 댓글 테스트", 10);
		NewsReply newsReply = createNewsReply(newsComment, member, "뉴스 대댓글 테스트");

		NewsReplyUpdateServiceRequest newsReplyUpdateServiceRequest = NewsReplyUpdateServiceRequest.builder()
			.newsReplyId(newsReply.getId())
			.comment("뉴스 대댓글 수정 테스트")
			.imgUrl("www.modifyTest.com")
			.build();

		Long updatedReplyId = newsReplyService.update(newsReplyUpdateServiceRequest);

		assertThat(newsReplyRepository.findById(updatedReplyId).get())
			.extracting("id", "newsComment.id", "member.publicId", "comment", "ip", "imgUrl")
			.contains(newsReply.getId(), newsComment.getId(), member.getPublicId(), "뉴스 대댓글 수정 테스트", "1.1.1.1",
				"www.modifyTest.com");
	}

	@DisplayName("뉴스 대댓글을 삭제한다.")
	@Test
	void deleteTest() {
		News news = createNews(1, NewsCategory.BASEBALL, 10);
		Member member = createMember(1);

		NewsComment newsComment = createNewsComment(news, member, "뉴스 댓글 테스트", 10);
		NewsReply newsReply = createNewsReply(newsComment, member, "뉴스 대댓글 테스트");
		Long deletedReplyId = newsReplyService.delete(newsReply.getId());

		assertAll(
			() -> assertThatThrownBy(() -> newsReplyService.delete(deletedReplyId))
				.isInstanceOf(PlayHiveException.class)
				.hasMessage(ErrorCode.NEWS_REPLY_NOT_FOUND.getMsg()),
			() -> assertThat(newsCountRepository.findByNewsId(news.getId()).get().getCommentCount()).isEqualTo(9)
		);
	}

}
