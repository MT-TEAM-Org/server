package org.myteam.server.news.newsReply.dto.controller.request;

import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.news.newsReply.dto.service.request.NewsReplyServiceRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
