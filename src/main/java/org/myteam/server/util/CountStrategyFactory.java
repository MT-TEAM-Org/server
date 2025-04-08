package org.myteam.server.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.myteam.server.board.util.BoardCountStrategy;
import org.myteam.server.improvement.util.ImprovementCountStrategy;
import org.myteam.server.inquiry.util.InquiryCountStrategy;
import org.myteam.server.news.util.NewsCountStrategy;
import org.myteam.server.notice.util.NoticeCountStrategy;
import org.myteam.server.report.domain.DomainType;
import org.springframework.stereotype.Component;

@Component
public class CountStrategyFactory {

    private final Map<DomainType, CountStrategy> strategies;

    public CountStrategyFactory(List<CountStrategy> strategyList) {
        strategies = new HashMap<>();
        for (CountStrategy strategy : strategyList) {
            if (strategy instanceof BoardCountStrategy) {
                strategies.put(DomainType.BOARD, strategy);
            } else if (strategy instanceof NewsCountStrategy) {
                strategies.put(DomainType.NEWS, strategy);
            } else if (strategy instanceof ImprovementCountStrategy) {
                strategies.put(DomainType.IMPROVEMENT, strategy);
            } else if (strategy instanceof NoticeCountStrategy) {
                strategies.put(DomainType.NOTICE, strategy);
            } else if (strategy instanceof InquiryCountStrategy) {
                strategies.put(DomainType.INQUIRY, strategy);
            }
            // 추가 전략이 있다면 여기에 등록
        }
    }

    public CountStrategy getStrategy(DomainType type) {
        return strategies.get(type);
    }
}