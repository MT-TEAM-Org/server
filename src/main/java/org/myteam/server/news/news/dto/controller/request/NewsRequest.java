package org.myteam.server.news.news.dto.controller.request;

import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.news.dto.service.request.NewsServiceRequest;
import org.myteam.server.news.news.repository.OrderType;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewsRequest extends PageInfoRequest {

	@NotNull(message = "뉴스 카테고리는 필수입니다.")
	private NewsCategory category;
	@NotNull(message = "뉴스 정렬 타입은 필수입니다.")
	private OrderType orderType;
	private String content;

	@Builder
	public NewsRequest(NewsCategory category, OrderType orderType, String content, int page, int size) {
		super(page, size);
		this.category = category;
		this.orderType = orderType;
		this.content = content;
	}

	public NewsServiceRequest toServiceRequest() {
		return NewsServiceRequest.builder()
			.category(category)
			.orderType(orderType)
			.content(content)
			.size(getSize())
			.page(getPage())
			.build();
	}
}
