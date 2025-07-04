package org.myteam.server.global.util.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.report.domain.DomainType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountScheduler {

    private final RedisCountBulkUpdater bulkUpdater;

    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 5 * 60 * 1000) // 실행 후 5분마다
    public void updateCounts() {
        try {
            CompletableFuture<Void> board = bulkUpdater.bulkUpdateAsync(DomainType.BOARD);
            CompletableFuture<Void> news = bulkUpdater.bulkUpdateAsync(DomainType.NEWS);
            CompletableFuture<Void> notice = bulkUpdater.bulkUpdateAsync(DomainType.NOTICE);
            CompletableFuture<Void> improvement = bulkUpdater.bulkUpdateAsync(DomainType.IMPROVEMENT);
            CompletableFuture<Void> inquiry = bulkUpdater.bulkUpdateAsync(DomainType.INQUIRY);

            CompletableFuture.allOf(board, news, notice, improvement, inquiry).get(1, TimeUnit.MINUTES); // 타임아웃 처리
        } catch (Exception e) {
            log.error("View count async update failed", e);
        }
    }
}