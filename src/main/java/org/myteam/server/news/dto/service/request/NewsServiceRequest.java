package org.myteam.server.news.dto.service.request;

import org.myteam.server.news.domain.NewsCategory;
import org.myteam.server.news.repository.OrderType;
import org.myteam.server.global.page.request.PageInfoServiceRequest;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsServiceRequest extends PageInfoServiceRequest {
	private NewsCategory category;
	private OrderType orderType;

	@Builder
	public NewsServiceRequest(NewsCategory category, OrderType orderType, int size, int page) {
		super(page, size);
		this.category = category;
		this.orderType = orderType;
	}

}
