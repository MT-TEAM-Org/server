package org.myteam.server.home.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.global.util.domain.TimePeriod;
import org.myteam.server.report.domain.DomainType;

@Getter
@Setter
@NoArgsConstructor
public class TotalSearchRequest extends PageInfoRequest {
    private DomainType domainType;
    private TimePeriod timePeriod;

    private BoardOrderType orderType;
    private BoardSearchType searchType;
    @NotNull
    private String search;

    @Builder
    public TotalSearchRequest(DomainType domainType, TimePeriod timePeriod, BoardOrderType orderType,
                              BoardSearchType searchType, String search) {
        this.domainType = domainType;
        this.timePeriod = timePeriod;
        this.orderType = orderType;
        this.searchType = searchType;
        this.search = search;
    }

    public TotalSearchServiceRequest toServiceRequest() {
        return TotalSearchServiceRequest.builder()
                .domainType(domainType)
                .timePeriod(timePeriod)
                .orderType(orderType)
                .searchType(searchType)
                .search(search)
                .size(getSize())
                .page(getPage())
                .build();
    }
}
