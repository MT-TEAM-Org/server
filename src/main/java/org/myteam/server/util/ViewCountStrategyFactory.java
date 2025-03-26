package org.myteam.server.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.myteam.server.board.util.BoardViewCountStrategy;
import org.myteam.server.improvement.util.ImprovementViewCountStrategy;
import org.myteam.server.news.util.NewsViewCountStrategy;
import org.myteam.server.notice.util.NoticeViewCountStrategy;
import org.springframework.stereotype.Component;

@Component
public class ViewCountStrategyFactory {

    private final Map<String, ViewCountStrategy> strategies;

    public ViewCountStrategyFactory(List<ViewCountStrategy> strategyList) {
        strategies = new HashMap<>();
        for (ViewCountStrategy strategy : strategyList) {
            if (strategy instanceof BoardViewCountStrategy) {
                strategies.put("board", strategy);
            } else if (strategy instanceof NewsViewCountStrategy) {
                strategies.put("news", strategy);
            } else if (strategy instanceof ImprovementViewCountStrategy) {
                strategies.put("improvement", strategy);
            } else if (strategy instanceof NoticeViewCountStrategy) {
                strategies.put("notice", strategy);
            }
            // 추가 전략이 있다면 여기에 등록
        }
    }

    public ViewCountStrategy getStrategy(String type) {
        return strategies.get(type);
    }
}