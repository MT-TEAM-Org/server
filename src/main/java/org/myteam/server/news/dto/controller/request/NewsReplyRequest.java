package org.myteam.server.news.dto.controller.request;

import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.news.dto.service.request.NewsCommentServiceRequest;
import org.myteam.server.news.dto.service.request.NewsReplyServiceRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsReplyRequest extends PageInfoRequest {

	@NotNull(message = "뉴스 대댓글 ID는 필수입니다.")
	private Long newsCommentId;

	@Builder
	public NewsReplyRequest(Long newsCommentId, int page, int size) {
		super(page, size);
		this.newsCommentId = newsCommentId;
	}

	public NewsReplyServiceRequest toServiceRequest() {
		return NewsReplyServiceRequest.builder()
			.newsCommentId(newsCommentId)
			.size(getSize())
			.page(getPage())
			.build();
	}
}
