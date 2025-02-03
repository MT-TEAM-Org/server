package org.myteam.server.news.dto.service.request;

import org.myteam.server.global.page.request.PageInfoServiceRequest;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsCommentServiceRequest extends PageInfoServiceRequest {

	private Long newsId;

	@Builder
	public NewsCommentServiceRequest(Long newsId, int size, int page) {
		super(page, size);
		this.newsId = newsId;
	}
}
