package org.myteam.server.global.util.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.report.domain.DomainType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountScheduler {

    private final RedisCountBulkUpdater bulkUpdater;

    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 5 * 60 * 1000) // 실행 후 5분마다
    public void updateCounts() {
        bulkUpdater.bulkUpdate(DomainType.BOARD);
        bulkUpdater.bulkUpdate(DomainType.NEWS);
        bulkUpdater.bulkUpdate(DomainType.NOTICE);
        bulkUpdater.bulkUpdate(DomainType.IMPROVEMENT);
        bulkUpdater.bulkUpdate(DomainType.INQUIRY);
    }
}