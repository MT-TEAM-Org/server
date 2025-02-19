package org.myteam.server.news.newsComment.dto.controller.request;

import org.myteam.server.news.newsComment.dto.service.request.NewsCommentUpdateServiceRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentUpdateRequest {

	@Schema(description = "뉴스 댓글 ID")
	@NotNull(message = "뉴스 댓글 ID는 필수입니다.")
	private Long newsCommentId;
	@Schema(description = "뉴스 댓글 내용")
	@NotNull(message = "뉴스 댓글 내용은 필수입니다.")
	private String comment;
	@Schema(description = "뉴스 댓글 이미지 (있을 시)")
	private String imgUrl;

	@Builder
	public NewsCommentUpdateRequest(Long newsCommentId, String comment, String imgUrl) {
		this.newsCommentId = newsCommentId;
		this.comment = comment;
		this.imgUrl = imgUrl;
	}

	public NewsCommentUpdateServiceRequest toServiceRequest() {
		return NewsCommentUpdateServiceRequest.builder()
			.newsCommentId(newsCommentId)
			.comment(comment)
			.imgUrl(imgUrl)
			.build();
	}
}
