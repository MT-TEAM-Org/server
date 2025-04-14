package org.myteam.server.news.news.dto.controller.request;

import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.global.util.domain.TimePeriod;
import org.myteam.server.news.news.dto.service.request.NewsServiceRequest;
import org.myteam.server.news.news.repository.OrderType;

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

	@Schema(description = "뉴스 정렬 타입", example = "DATE(최신), VIEW(인기), COMMENT(댓글)")
	@NotNull(message = "뉴스 정렬 타입은 필수입니다.")
	private OrderType orderType;

	@Schema(description = "뉴스 검색 타입", example = "TITLE(제목), CONTENT(내용), TITLE_CONTENT(제목 + 내용), COMMENT(댓글)")
	private BoardSearchType searchType;

	@Schema(description = "검색할 뉴스 내용", example = "검색할 뉴스 제목")
	private String search;

	@Schema(description = "날짜 조건", example = "DAILY(일별), WEEKLY(주별), MONTHLY(월별), YEARLY(년별)")
	private TimePeriod timePeriod;

	@Schema(description = "시작할 인덱스 번호")
	private int startIndex;


	@Builder
	public NewsRequest(Category category, OrderType orderType, BoardSearchType SearchType, String search,
		TimePeriod timePeriod, int page,
		int size, int startIndex) {
		super(page, size);
		this.category = category;
		this.orderType = orderType;
		this.searchType = SearchType;
		this.search = search;
		this.timePeriod = timePeriod;
		this.startIndex = startIndex;
	}

	public NewsServiceRequest toServiceRequest() {
		return NewsServiceRequest.builder()
			.category(category)
			.orderType(orderType)
			.searchType(searchType)
			.search(search)
			.timePeriod(timePeriod)
			.size(getSize())
			.page(getPage())
			.startIndex(startIndex)
			.build();
	}
}
