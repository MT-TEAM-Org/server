package org.myteam.server.improvement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.util.redis.RedisCountService;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.report.domain.DomainType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImprovementCountService {

    private final RedisCountService redisCountService;

    public void recommendImprovement(Long improvementId) {
        log.info("개선요청: {} 추천 요청", improvementId);

        redisCountService.getCommonCount(ServiceType.RECOMMEND, DomainType.IMPROVEMENT, improvementId, null);

        log.info("개선요청: {} 추천 성공", improvementId);
    }

    public void deleteRecommendImprovement(Long improvementId) {
        log.info("개선요청: {} 삭제 요청", improvementId);

        redisCountService.getCommonCount(ServiceType.RECOMMEND_CANCEL, DomainType.IMPROVEMENT, improvementId, null);

        log.info("개선요청: {} 삭제 성공", improvementId);
    }
}
