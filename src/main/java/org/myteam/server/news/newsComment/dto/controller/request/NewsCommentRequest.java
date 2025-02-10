package org.myteam.server.news.newsComment.dto.controller.request;

import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.news.newsComment.dto.service.request.NewsCommentServiceRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewsCommentRequest extends PageInfoRequest {

	@Schema(description = "뉴스 ID")
	@NotNull(message = "뉴스ID는 필수입니다.")
	private Long newsId;

	@Builder
	public NewsCommentRequest(Long newsId, int page, int size) {
		super(page, size);
		this.newsId = newsId;
	}

	public NewsCommentServiceRequest toServiceRequest() {
		return NewsCommentServiceRequest.builder()
			.newsId(newsId)
			.size(getSize())
			.page(getPage())
			.build();
	}
}
