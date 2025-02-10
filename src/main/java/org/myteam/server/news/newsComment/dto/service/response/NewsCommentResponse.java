package org.myteam.server.news.newsComment.dto.service.response;

import java.time.LocalDateTime;

import org.myteam.server.member.entity.Member;
import org.myteam.server.news.newsComment.domain.NewsComment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentResponse {

	@Schema(description = "뉴스 댓글 ID")
	private Long newsCommentId;
	@Schema(description = "뉴스 ID")
	private Long newsId;
	@Schema(description = "뉴스 작성자")
	private NewsCommentMemberResponse member;
	@Schema(description = "뉴스 댓글 내용")
	private String comment;
	@Schema(description = "뉴스 댓글 작성 날짜")
	private LocalDateTime createDate;

	@Builder
	public NewsCommentResponse(Long newsCommentId, Long newsId, NewsCommentMemberResponse member, String comment, LocalDateTime createDate) {
		this.newsCommentId = newsCommentId;
		this.newsId = newsId;
		this.member = member;
		this.comment = comment;
		this.createDate = createDate;
	}

	public static NewsCommentResponse createResponse(NewsComment newsComment, Member member) {
		return NewsCommentResponse.builder()
			.newsCommentId(newsComment.getId())
			.newsId(newsComment.getNews().getId())
			.member(
				NewsCommentMemberResponse.builder()
					.memberPublicId(member.getPublicId())
					.nickName(member.getNickname())
					.build()
			)
			.comment(newsComment.getComment())
			.createDate(newsComment.getCreateDate())
			.build();
	}
}
