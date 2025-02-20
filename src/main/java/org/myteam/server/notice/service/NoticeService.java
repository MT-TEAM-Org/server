package org.myteam.server.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.notice.Repository.NoticeCountRepository;
import org.myteam.server.notice.Repository.NoticeRepository;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.domain.NoticeCount;
import org.myteam.server.notice.dto.request.NoticeRequest.*;
import org.myteam.server.notice.dto.response.NoticeResponse.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeCountRepository noticeCountRepository;
    private final SecurityReadService securityReadService;
    private final NoticeRecommendReadService noticeRecommendReadService;

    /**
     * 공지사항 작성
     */
    public NoticeSaveResponse saveNotice(NoticeSaveResquest request, String clientIp) {
        log.info("save Notice 실행");
        Member member = securityReadService.getMember();

        if (member.isAdmin()) {
            throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
        }

        Notice notice = makeNotice(member, clientIp, request);

        NoticeCount noticeCount = NoticeCount.createNoticeCount(notice);
        noticeCountRepository.save(noticeCount);

        boolean isRecommended = noticeRecommendReadService.isRecommended(notice.getId(), member.getPublicId());

        log.info("공지사항 생성: {}", notice.getId());
        return NoticeSaveResponse.createResponse(notice, noticeCount, isRecommended);

    }

    private Notice makeNotice(final Member member, String clientIp, NoticeSaveResquest resquest) {
        Notice notice = Notice.builder()
                .member(member)
                .createdIP(clientIp)
                .title(resquest.getTitle())
                .content(resquest.getContent())
                .imgUrl(resquest.getImgUrl())
                .build();

        noticeRepository.save(notice);
        return notice;
    }
}
