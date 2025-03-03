package org.myteam.server.news.news.dto.service.request;

import org.myteam.server.global.page.request.PageInfoServiceRequest;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.news.repository.OrderType;
import org.myteam.server.news.news.repository.TimePeriod;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsServiceRequest extends PageInfoServiceRequest {
	private NewsCategory category;
	private OrderType orderType;
	private String content;
	private TimePeriod timePeriod;

	@Builder
	public NewsServiceRequest(NewsCategory category, OrderType orderType, String content, TimePeriod timePeriod, int size, int page) {
		super(page, size);
		this.category = category;
		this.orderType = orderType;
		this.content = content;
		this.timePeriod = timePeriod;
	}

}
