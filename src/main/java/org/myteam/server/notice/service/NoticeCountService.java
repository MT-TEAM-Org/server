package org.myteam.server.notice.service;

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
public class NoticeCountService {
    private final RedisCountService redisCountService;

    public void recommendNotice(Long noticeId) {
        log.info("공지사항: {} 추천 요청", noticeId);
        redisCountService.getCommonCount(ServiceType.RECOMMEND, DomainType.NOTICE, noticeId, null);
        log.info("공지사항: {} 추천 성공", noticeId);
    }

    public void deleteRecommendNotice(Long noticeId) {
        log.info("공지사항: {} 추천 삭제 요청", noticeId);
        redisCountService.getCommonCount(ServiceType.RECOMMEND_CANCEL, DomainType.NOTICE, noticeId, null);
        log.info("공지사항: {} 추천 삭제 성공", noticeId);
    }
}
