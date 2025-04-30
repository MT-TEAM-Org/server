package org.myteam.server.notice.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.aop.CountView;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.RedisCountService;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.dto.request.NoticeRequest.NoticeServiceRequest;
import org.myteam.server.notice.dto.response.NoticeResponse.NoticeDto;
import org.myteam.server.notice.dto.response.NoticeResponse.NoticeListResponse;
import org.myteam.server.notice.dto.response.NoticeResponse.NoticeSaveResponse;
import org.myteam.server.notice.repository.NoticeQueryRepository;
import org.myteam.server.notice.repository.NoticeRepository;
import org.myteam.server.report.domain.DomainType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeReadService {

    private final NoticeRepository noticeRepository;
    private final NoticeRecommendReadService noticeRecommendReadService;
    private final NoticeQueryRepository noticeQueryRepository;
    private final SecurityReadService securityReadService;
    private final RedisCountService redisCountService;

    public Notice findById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.NOTICE_NOT_FOUND));
    }

    /**
     * 공지사항 상세 조회
     */
    @CountView(domain = DomainType.NOTICE, idParam = "noticeId")
    public NoticeSaveResponse getNotice(Long noticeId) {
        log.info("공지사항: {} 상세 조회 호출", noticeId);

        Notice notice = findById(noticeId);
        UUID memberPublicId = securityReadService.getAuthenticatedPublicId();

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NOTICE,
                notice.getId(), null);

        boolean isRecommended = false;

        if (memberPublicId != null) {
            isRecommended = noticeRecommendReadService.isRecommended(notice.getId(), memberPublicId);
        }

        log.info("공지사항 상세 조회 성공: {}", noticeId);

        Long previousId = noticeQueryRepository.findPreviousNoticeId(notice.getId());
        Long nextId = noticeQueryRepository.findNextNoticeId(notice.getId());

        return NoticeSaveResponse.createResponse(notice, isRecommended, previousId, nextId, commonCountDto);
    }

    /**
     * 공지사항 목록 조회
     */
    public NoticeListResponse getNoticeList(NoticeServiceRequest request) {
        log.info("공지사항 목록 조회 호출");
        Page<NoticeDto> noticePagingList = noticeQueryRepository.getNoticeList(
                request.getSearchType(),
                request.getSearch(),
                request.toPageable()
        );

        log.info("공지사항 목록 조회 성공");

        return NoticeListResponse.createResponse(PageCustomResponse.of(noticePagingList));
    }

    public boolean existsById(Long commentId) {
        return noticeRepository.existsById(commentId);
    }
}
