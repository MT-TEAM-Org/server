package org.myteam.server.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.myteam.server.board.util.BoardCountStrategy;
import org.myteam.server.improvement.util.ImprovementCountStrategy;
import org.myteam.server.news.util.NewsCountStrategy;
import org.myteam.server.notice.util.NoticeCountStrategy;
import org.springframework.stereotype.Component;

@Component
public class CountStrategyFactory {

    private final Map<String, CountStrategy> strategies;

    public CountStrategyFactory(List<CountStrategy> strategyList) {
        strategies = new HashMap<>();
        for (CountStrategy strategy : strategyList) {
            if (strategy instanceof BoardCountStrategy) {
                strategies.put("board", strategy);
            } else if (strategy instanceof NewsCountStrategy) {
                strategies.put("news", strategy);
            } else if (strategy instanceof ImprovementCountStrategy) {
                strategies.put("improvement", strategy);
            } else if (strategy instanceof NoticeCountStrategy) {
                strategies.put("notice", strategy);
            }
            // 추가 전략이 있다면 여기에 등록
        }
    }

    public CountStrategy getStrategy(String type) {
        return strategies.get(type);
    }
}