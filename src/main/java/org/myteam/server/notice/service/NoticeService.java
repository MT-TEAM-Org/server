package org.myteam.server.notice.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.service.CommentService;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.media.MediaUtils;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.global.util.redis.service.RedisCountService;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.domain.NoticeCount;
import org.myteam.server.notice.dto.request.NoticeRequest.NoticeSaveRequest;
import org.myteam.server.notice.dto.response.NoticeResponse.NoticeSaveResponse;
import org.myteam.server.notice.repository.NoticeCountRepository;
import org.myteam.server.notice.repository.NoticeQueryRepository;
import org.myteam.server.notice.repository.NoticeRepository;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.upload.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeCountRepository noticeCountRepository;
    private final NoticeQueryRepository noticeQueryRepository;
    private final SecurityReadService securityReadService;
    private final NoticeRecommendReadService noticeRecommendReadService;
    private final NoticeReadService noticeReadService;
    private final CommentService commentService;
    private final StorageService s3Service;
    private final RedisCountService redisCountService;

    /**
     * 공지사항 작성
     */
    public NoticeSaveResponse saveNotice(NoticeSaveRequest request, String clientIp) {
        log.info("save Notice 실행");
        Member member = securityReadService.getMember();

        if (!member.isAdmin()) {
            throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
        }

        Notice notice = makeNotice(member, clientIp, request);

        NoticeCount noticeCount = NoticeCount.createNoticeCount(notice);
        noticeCountRepository.save(noticeCount);

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NOTICE,
                notice.getId(), null);

        boolean isRecommended = noticeRecommendReadService.isRecommended(notice.getId(), member.getPublicId());

        log.info("공지사항 생성: {}", notice.getId());

        Long previousId = noticeQueryRepository.findPreviousNoticeId(notice.getId());
        Long nextId = noticeQueryRepository.findNextNoticeId(notice.getId());

        return NoticeSaveResponse.createResponse(notice, isRecommended, previousId, nextId, commonCountDto);

    }

    private Notice makeNotice(final Member member, String clientIp, NoticeSaveRequest request) {
        Notice notice = Notice.builder()
                .member(member)
                .createdIp(clientIp)
                .title(request.getTitle())
                .content(request.getContent())
                .imgUrl(request.getImgUrl())
                .link(request.getLink())
                .build();

        noticeRepository.save(notice);
        return notice;
    }

    /**
     * 공지사항 수정
     */
    public NoticeSaveResponse updateNotice(NoticeSaveRequest request, Long noticeId) {
        log.info("update Notice 실행");

        Member member = securityReadService.getMember();
        if (!member.isAdmin()) {
            throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
        }

        Notice notice = noticeReadService.findById(noticeId);
        if (notice.getMember().getPublicId() != member.getPublicId()) {
            throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
        }

        if (MediaUtils.verifyImageUrlAndRequestImageUrl(notice.getImgUrl(), request.getImgUrl())) {
            s3Service.deleteFile(notice.getImgUrl());
        }

        notice.updateNotice(request.getTitle(), request.getContent(), request.getImgUrl(), request.getLink());
        noticeRepository.save(notice);

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.NOTICE,
                notice.getId(), null);

        boolean isRecommended = noticeRecommendReadService.isRecommended(notice.getId(), member.getPublicId());

        log.info("공지사항 수정: {}", notice.getId());

        Long previousId = noticeQueryRepository.findPreviousNoticeId(notice.getId());
        Long nextId = noticeQueryRepository.findNextNoticeId(notice.getId());

        return NoticeSaveResponse.createResponse(notice, isRecommended, previousId, nextId, commonCountDto);
    }

    /**
     * 공지사항 삭제
     */
    public void deleteNotice(List<Long> noticeIdList) {
        log.info("delete Notice 실행");

        Member member = securityReadService.getMember();
        if (!member.isAdmin()) {
            throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
        }

        for (Long noticeId : noticeIdList) {

            Notice notice = noticeReadService.findById(noticeId);
            if (notice.getMember().getPublicId() != member.getPublicId()) {
                throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
            }

            if (notice.getImgUrl() != null) {
                s3Service.deleteFile(notice.getImgUrl());
            }

            redisCountService.removeCount(DomainType.NOTICE, noticeId);

            noticeCountRepository.deleteByNoticeId(notice.getId());
            noticeRepository.delete(notice);

            log.info("공지사항 삭제: {}", noticeId);

            commentService.deleteCommentByPost(CommentType.NOTICE, noticeId);
        }
    }
}