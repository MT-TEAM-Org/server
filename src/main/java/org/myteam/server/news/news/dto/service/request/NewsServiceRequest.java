package org.myteam.server.news.news.dto.service.request;

import org.myteam.server.global.domain.Category;
import org.myteam.server.global.page.request.PageInfoServiceRequest;
import org.myteam.server.global.util.domain.TimePeriod;
import org.myteam.server.news.news.repository.OrderType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsServiceRequest extends PageInfoServiceRequest {
	private Category category;
	private OrderType orderType;
	private String content;
	private TimePeriod timePeriod;

	@Builder
	public NewsServiceRequest(Category category, OrderType orderType, String content, TimePeriod timePeriod, int size, int page) {
		super(page, size);
		this.category = category;
		this.orderType = orderType;
		this.content = content;
		this.timePeriod = timePeriod;
	}

}
