package org.myteam.server.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
<<<<<<< HEAD
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.notice.Repository.NoticeCountRepository;
import org.myteam.server.notice.Repository.NoticeQueryRepository;
import org.myteam.server.notice.Repository.NoticeRepository;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.domain.NoticeCount;
import org.myteam.server.notice.dto.request.NoticeRequest.NoticeServiceRequest;
import org.myteam.server.notice.dto.response.NoticeResponse.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

=======
import org.myteam.server.notice.Repository.NoticeRepository;
import org.myteam.server.notice.domain.Notice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

>>>>>>> 602a42f (feat: 공지사항 수정)
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeReadService {

    private final NoticeRepository noticeRepository;
<<<<<<< HEAD
    private final NoticeCountReadService noticeCountReadService;
    private final MemberRepository memberRepository;
    private final NoticeRecommendReadService noticeRecommendReadService;
    private final NoticeQueryRepository noticeQueryRepository;
=======
>>>>>>> 602a42f (feat: 공지사항 수정)

    public Notice findById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.NOTICE_NOT_FOUND));
    }
<<<<<<< HEAD

    /**
     * 공지사항 상세 조회
     */
    public NoticeSaveResponse getNotice(Long noticeId, CustomUserDetails userDetails) {
        log.info("공지사항: {} 상세 조회 호출", noticeId);

        Notice notice = findById(noticeId);
        NoticeCount noticeCount = noticeCountReadService.findByNoticeId(noticeId);

        boolean isRecommended = false;

        if (userDetails != null) {
            UUID memberPublicId = memberRepository.findByPublicId(userDetails.getPublicId()).get().getPublicId();
            isRecommended = noticeRecommendReadService.isRecommended(notice.getId(), memberPublicId);
        }

        log.info("공지사항 상세 조회 성공: {}", noticeId);

        return NoticeSaveResponse.createResponse(notice, noticeCount, isRecommended);
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

=======
>>>>>>> 602a42f (feat: 공지사항 수정)
}
