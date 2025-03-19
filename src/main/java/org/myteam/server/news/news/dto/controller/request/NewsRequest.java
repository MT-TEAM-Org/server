package org.myteam.server.news.news.dto.controller.request;

import org.myteam.server.global.domain.Category;
import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.news.news.dto.service.request.NewsServiceRequest;
import org.myteam.server.news.news.repository.OrderType;
import org.myteam.server.news.news.repository.TimePeriod;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewsRequest extends PageInfoRequest {

	@Schema(description = "뉴스 카테고리", example = "BASEBALL, FOOTBALL, ESPORTS")
	private Category category;

	@Schema(description = "뉴스 정렬 타입", example = "DATE(날짜순), COMMENT(댓글순), VIEW(조회순)")
	@NotNull(message = "뉴스 정렬 타입은 필수입니다.")
	private OrderType orderType;

	@Schema(description = "검색할 뉴스 내용", example = "검색할 뉴스 제목")
	private String content;

	@Schema(description = "날짜 조건", example = "DAILY(일별), WEEKLY(주별), MONTHLY(월별), YEARLY(년별)")
	private TimePeriod timePeriod;

	@Builder
	public NewsRequest(Category category, OrderType orderType, String content, TimePeriod timePeriod, int page,
		int size) {
		super(page, size);
		this.category = category;
		this.orderType = orderType;
		this.content = content;
		this.timePeriod = timePeriod;
	}

	public NewsServiceRequest toServiceRequest() {
		return NewsServiceRequest.builder()
			.category(category)
			.orderType(orderType)
			.content(content)
			.timePeriod(timePeriod)
			.size(getSize())
			.page(getPage())
			.build();
	}
}
