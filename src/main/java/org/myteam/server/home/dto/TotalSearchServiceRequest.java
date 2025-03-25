package org.myteam.server.home.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.global.page.request.PageInfoServiceRequest;
import org.myteam.server.global.util.domain.TimePeriod;
import org.myteam.server.report.domain.DomainType;

@Getter
@NoArgsConstructor
public class TotalSearchServiceRequest extends PageInfoServiceRequest {
    private DomainType domainType;
    private TimePeriod timePeriod;

    private BoardOrderType orderType;
    private BoardSearchType searchType;
    private String search;

    @Builder
    public TotalSearchServiceRequest(DomainType domainType, TimePeriod timePeriod, BoardOrderType orderType,
                                     BoardSearchType searchType, String search, int size, int page) {
        super(page, size);
        this.domainType = domainType;
        this.timePeriod = timePeriod;
        this.orderType = orderType;
        this.searchType = searchType;
        this.search = search;
    }
}
