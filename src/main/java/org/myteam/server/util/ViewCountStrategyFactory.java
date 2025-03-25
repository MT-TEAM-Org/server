package org.myteam.server.util;

import org.myteam.server.board.util.BoardViewCountStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ViewCountStrategyFactory {

    private final Map<String, ViewCountStrategy> strategies;

    public ViewCountStrategyFactory(List<ViewCountStrategy> strategyList) {
        strategies = new HashMap<>();
        for (ViewCountStrategy strategy : strategyList) {
            if (strategy instanceof BoardViewCountStrategy) {
                strategies.put("board", strategy);
            }
            // 추가 전략이 있다면 여기에 등록
        }
    }

    public ViewCountStrategy getStrategy(String type) {
        return strategies.get(type);
    }
}

