package org.myteam.server.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.BadWordFilter;
import org.myteam.server.global.util.upload.MediaUtils;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.notice.Repository.NoticeReplyRepository;
import org.myteam.server.notice.domain.NoticeComment;
import org.myteam.server.notice.domain.NoticeReply;
import org.myteam.server.notice.dto.request.NoticeCommentRequest.*;
import org.myteam.server.notice.dto.response.NoticeCommentResponse.*;
import org.myteam.server.notice.service.NoticeCommentReadService;
import org.myteam.server.notice.service.NoticeCountService;
import org.myteam.server.notice.service.NoticeReplyReadService;
import org.myteam.server.notice.service.NoticeReplyRecommendReadService;
import org.myteam.server.upload.service.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeReplyService {

    private final SecurityReadService securityReadService;
    private final NoticeCommentReadService noticeCommentReadService;
    private final MemberReadService memberReadService;
    private final BadWordFilter badWordFilter;
    private final NoticeCountService noticeCountService;
    private final NoticeReplyRecommendReadService noticeReplyRecommendReadService;
    private final NoticeReplyReadService noticeReplyReadService;
    private final S3Service s3Service;
    private final NoticeReplyRepository noticeReplyRepository;

    /**
     * 공지사항 대댓글 생성
     */
    public NoticeReplyResponse saveReply(Long noticeCommentId, NoticeReplySaveRequest request, String createdIp) {
        Member member = securityReadService.getMember();
        NoticeComment noticeComment = noticeCommentReadService.findById(noticeCommentId);

        Member mentionedMember = request.getMentionedPublicId() != null ?
                memberReadService.findById(request.getMentionedPublicId()) : null;

        NoticeReply noticeReply = NoticeReply.createNoticeReply(noticeComment, member, request.getImageUrl(),
                badWordFilter.filterMessage(request.getComment()), createdIp, mentionedMember);

        noticeCountService.addCommentCount(noticeComment.getNotice().getId());

        boolean isRecommended = noticeReplyRecommendReadService.isRecommended(noticeReply.getId(), member.getPublicId());

        return NoticeReplyResponse.createResponse(noticeReply, member, mentionedMember, isRecommended);
    }

    /**
     * 공지사항 대댓글 수정
     */
    public NoticeReplyResponse update(Long noticeReplyId, NoticeReplySaveRequest request) {
        Member member = securityReadService.getMember();
        NoticeReply noticeReply = noticeReplyReadService.findById(noticeReplyId);

        noticeReply.verifyNoticeReplyAuthor(member);
        if (!MediaUtils.verifyImageUrlAndRequestImageUrl(noticeReply.getImageUrl(), request.getImageUrl())) {
            // 기존 이미지와 요청 이미지가 같지 않으면 삭제
            s3Service.deleteFile(MediaUtils.getImagePath(request.getImageUrl()));
        }

        Member mentionedMember = request.getMentionedPublicId() != null ?
                memberReadService.findById(request.getMentionedPublicId()) : null;

        noticeReply.updateReply(request.getImageUrl(), badWordFilter.filterMessage(request.getComment()), mentionedMember);
        noticeReplyRepository.save(noticeReply);

        boolean isRecommended = noticeReplyRecommendReadService.isRecommended(noticeReply.getId(), member.getPublicId());

        return NoticeReplyResponse.createResponse(noticeReply, member, mentionedMember, isRecommended);
    }

    /**
     * 공지사항 대댓글 삭제
     */
    public void delete(Long noticeReplyId) {
        Member member = securityReadService.getMember();
        NoticeReply noticeReply = noticeReplyReadService.findById(noticeReplyId);

        noticeReply.verifyNoticeReplyAuthor(member);

        s3Service.deleteFile(MediaUtils.getImagePath(noticeReply.getImageUrl()));
        noticeReplyRepository.delete(noticeReply);

        noticeCountService.minusCommentCount(noticeReply.getNoticeComment().getNotice().getId());
    }
}
