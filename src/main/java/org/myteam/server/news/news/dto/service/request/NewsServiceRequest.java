package org.myteam.server.news.news.dto.service.request;

import org.myteam.server.board.domain.BoardSearchType;
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
	private BoardSearchType searchType;
	private String search;
	private TimePeriod timePeriod;
	private int startIndex;

	@Builder
	public NewsServiceRequest(Category category, OrderType orderType, BoardSearchType searchType, String search,
		TimePeriod timePeriod, int size, int page, int startIndex) {
		super(page, size);
		this.category = category;
		this.orderType = orderType;
		this.searchType = searchType;
		this.search = search;
		this.timePeriod = timePeriod;
		this.startIndex = startIndex;
	}

}
