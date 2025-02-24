package org.myteam.server.news.newsReply.dto.controller.request;

import org.myteam.server.news.newsReply.dto.service.request.NewsReplyUpdateServiceRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyUpdateRequest {

	@Schema(description = "뉴스 대댓글 ID")
	@NotNull(message = "뉴스 대댓글 ID는 필수입니다.")
	private Long newsReplyId;
	@Schema(description = "뉴스 대댓글 내용 ID")
	@NotNull(message = "뉴스 대댓글 내용은 필수입니다.")
	private String comment;
	@Schema(description = "뉴스 대댓글 이미지 (삭제면 null)")
	private String imgUrl;

	@Builder
	public NewsReplyUpdateRequest(Long newsReplyId, String comment, String imgUrl) {
		this.newsReplyId = newsReplyId;
		this.comment = comment;
		this.imgUrl = imgUrl;
	}

	public NewsReplyUpdateServiceRequest toServiceRequest() {
		return NewsReplyUpdateServiceRequest.builder()
			.newsReplyId(newsReplyId)
			.comment(comment)
			.imgUrl(imgUrl)
			.build();
	}
}
