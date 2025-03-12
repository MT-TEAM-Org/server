package org.myteam.server.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.notice.repository.NoticeCommentQueryRepository;
import org.myteam.server.notice.repository.NoticeCommentRepository;
import org.myteam.server.notice.domain.NoticeComment;
import org.myteam.server.notice.dto.response.NoticeCommentResponse.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeCommentReadService {

    private final NoticeCommentRepository noticeCommentRepository;
    private final MemberRepository memberRepository;
    private final NoticeCommentQueryRepository noticeCommentQueryRepository;
    private final NoticeCommentRecommendReadService noticeCommentRecommendReadService;

    public NoticeComment findById(Long noticeCommentId) {
        return noticeCommentRepository.findById(noticeCommentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.NOTICE_COMMENT_NOT_FOUND));
    }

    public NoticeCommentListResponse findByNoticeId(Long noticeId, CustomUserDetails userDetails) {
        log.info("공지사항 {} 댓글 목록 조회 시도", noticeId);
        List<NoticeCommentSaveResponse> list = noticeCommentQueryRepository.getNoticeCommentList(
                noticeId,
                userDetails);
        log.info("공지사항 {} 댓글 목록 조회 성공", noticeId);

        return NoticeCommentListResponse.createResponse(list);
    }

    public NoticeCommentSaveResponse findByIdWithReply(Long noticeCommentId, CustomUserDetails userDetails) {
        log.info("공지사항 댓글: {} 상세 조회 시도", noticeCommentId);
        NoticeComment noticedComment = findById(noticeCommentId);

        boolean issRecommended = false;

        if (userDetails != null) {
            UUID loginUser = memberRepository.findByPublicId(userDetails.getPublicId()).get().getPublicId();
            issRecommended = noticeCommentRecommendReadService.isRecommended(noticedComment.getId(), loginUser);
        }

        NoticeCommentSaveResponse response = NoticeCommentSaveResponse.createResponse(noticedComment, noticedComment.getMember(),
                issRecommended);

        response.setNoticeReplyList(noticeCommentQueryRepository.getRepliesForComments(noticedComment.getId(), userDetails));
        log.info("공지사항 댓글: {} 상세 조회 성공", noticeCommentId);

        return response;
    }

    public int getCommentCountByMemberPublicId(UUID publicId) {
        return noticeCommentQueryRepository.getCommentCountByPublicId(publicId);
    }

    public List<NoticeCommentSaveResponse> findBestByNoticeId(Long noticeId) {
        return noticeCommentQueryRepository.getNoticeBestCommentList(noticeId);
    }
}
