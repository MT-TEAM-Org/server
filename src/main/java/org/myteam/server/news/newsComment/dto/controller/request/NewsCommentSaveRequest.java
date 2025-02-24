package org.myteam.server.news.newsComment.dto.controller.request;

import org.myteam.server.news.newsComment.dto.service.request.NewsCommentSaveServiceRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentSaveRequest {

	@Schema(description = "뉴스 ID")
	@NotNull(message = "뉴스ID는 필수입니다.")
	private Long newsId;
	@Schema(description = "댓글")
	@NotNull(message = "뉴스 댓글은 필수입니다.")
	private String comment;
	@Schema(description = "사진 URL (있을 시)")
	private String imgUrl;

	@Builder
	public NewsCommentSaveRequest(Long newsId, String comment, String imgUrl) {
		this.newsId = newsId;
		this.comment = comment;
		this.imgUrl = imgUrl;
	}

	public NewsCommentSaveServiceRequest toServiceRequest(String ip) {
		return NewsCommentSaveServiceRequest.builder()
			.newsId(newsId)
			.comment(comment)
			.ip(ip)
			.imgUrl(imgUrl)
			.build();
	}
}
