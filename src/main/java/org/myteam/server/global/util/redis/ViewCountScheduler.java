package org.myteam.server.global.util.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountScheduler {

    private final RedisViewCountBulkUpdater bulkUpdater;

    @Scheduled(fixedRate = 5 * 60 * 1000) // 5분마다
    public void updateBoardViews() {
        bulkUpdater.bulkUpdate("board");
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void updateNewsViews() {
        bulkUpdater.bulkUpdate("news");
    }
}
