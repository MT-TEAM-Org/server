package org.myteam.server.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.global.util.upload.MediaUtils;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.notice.repository.NoticeCommentRepository;
import org.myteam.server.notice.repository.NoticeReplyRepository;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.domain.NoticeComment;
import org.myteam.server.notice.domain.NoticeReply;
import org.myteam.server.notice.dto.request.NoticeCommentRequest.*;
import org.myteam.server.notice.dto.response.NoticeCommentResponse.*;
import org.myteam.server.upload.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeCommentService {

    private final NoticeReadService noticeReadService;
    private final SecurityReadService securityReadService;
    private final BadWordFilter badWordFilter;
    private final NoticeCommentRepository noticeCommentRepository;
    private final NoticeCountService noticeCountService;
    private final NoticeRecommendReadService noticeRecommendReadService;
    private final NoticeCommentReadService noticeCommentReadService;
    private final S3Service s3Service;
    private final NoticeCommentRecommendReadService noticeCommentRecommendReadService;
    private final NoticeReplyRepository noticeReplyRepository;
    private final NoticeReplyReadService noticeReplyReadService;

    /**
     * 공지사항 댓글 생성
     */
    public NoticeCommentSaveResponse save(Long noticeId, NoticeCommentSaveRequest request, String createdIp) {
        log.info("공지사항: {}의 댓글 생성 시도", noticeId);
        Notice notice = noticeReadService.findById(noticeId);
        Member member = securityReadService.getMember();

        NoticeComment noticeComment = NoticeComment.createNoticeComment(notice, member,
                request.getImageUrl(), badWordFilter.filterMessage(request.getComment()), createdIp);

        noticeCommentRepository.save(noticeComment);
        noticeCountService.addCommentCount(notice.getId());

        boolean isRecommended = noticeRecommendReadService.isRecommended(noticeComment.getId(), member.getPublicId());

        log.info("공지사항: {}의 댓글 생성 성공", noticeId);

        return NoticeCommentSaveResponse.createResponse(noticeComment, member, isRecommended);
    }

    /**
     * 공지사항 댓글 수정
     */
    public NoticeCommentSaveResponse update(Long noticeCommentId, NoticeCommentUpdateRequest request) {
        log.info("공지사항 댓글: {} 수정 시도", noticeCommentId);
        Member member = securityReadService.getMember();
        NoticeComment noticeComment = noticeCommentReadService.findById(noticeCommentId);

        noticeComment.verifyNoticeCommentAuthor(member);
        if (!MediaUtils.verifyImageUrlAndRequestImageUrl(noticeComment.getImageUrl(), request.getImageUrl())) {
            s3Service.deleteFile(MediaUtils.getImagePath(request.getImageUrl()));
        }

        noticeComment.updateComment(request.getImageUrl(), badWordFilter.filterMessage(request.getComment()));

        boolean isRecommended = noticeCommentRecommendReadService.isRecommended(noticeComment.getId(), member.getPublicId());

        log.info("공지사항 댓글: {} 수정 성공", noticeCommentId);

        return NoticeCommentSaveResponse.createResponse(noticeComment, member, isRecommended);
    }

    /**
     * 공지사항 댓글 삭제
     */
    public void deleteNoticeComment(Long noticeCommentId) {
        log.info("공지사항 댓글: {} 삭제 시도", noticeCommentId);
        Member member = securityReadService.getMember();
        NoticeComment noticeComment = noticeCommentReadService.findById(noticeCommentId);

        noticeComment.verifyNoticeCommentAuthor(member);

        // S3 이미지 삭제
        if (noticeComment.getImageUrl() != null) {
            s3Service.deleteFile(MediaUtils.getImagePath(noticeComment.getImageUrl()));
        }
        // 대댓글 삭제 (카운트도 포함)
        int minusCount = deleteNoticeReply(noticeComment.getId());
        // 댓글 삭제
        noticeCommentRepository.deleteById(noticeCommentId);

        // 댓글 카운트 감소
        noticeCountService.minusCommentCount(noticeComment.getNotice().getId(), minusCount + 1);

        log.info("공지사항 댓글: {} 삭제 성공", noticeCommentId);
    }

    /**
     * 대댓글 삭제
     */
    private int deleteNoticeReply(Long noticeCommentId) {
        List<NoticeReply> noticeReplyList = noticeReplyReadService.findByNoticeCommentId(noticeCommentId);
        for (NoticeReply noticeReply : noticeReplyList) {
            s3Service.deleteFile(MediaUtils.getImagePath(noticeReply.getImageUrl()));
            noticeReplyRepository.delete(noticeReply);
        }
        return noticeReplyList.size();
    }
}
