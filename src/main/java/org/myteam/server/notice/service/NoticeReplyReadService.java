//package org.myteam.server.notice.service;
//
//import lombok.RequiredArgsConstructor;
//import org.myteam.server.global.exception.ErrorCode;
//import org.myteam.server.global.exception.PlayHiveException;
//import org.myteam.server.notice.repository.NoticeCommentQueryRepository;
//import org.myteam.server.notice.repository.NoticeReplyRepository;
//import org.myteam.server.notice.domain.NoticeReply;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class NoticeReplyReadService {
//
//    private final NoticeReplyRepository noticeReplyRepository;
//    private final NoticeCommentQueryRepository noticeCommentQueryRepository;
//
//    public NoticeReply findById(Long noticeReplyId) {
//        return noticeReplyRepository.findById(noticeReplyId)
//                .orElseThrow(() -> new PlayHiveException(ErrorCode.NOTICE_REPLY_NOT_FOUND));
//    }
//
//    public List<NoticeReply> findByNoticeCommentId(Long noticeCommentId) {
//        return noticeReplyRepository.findByNoticeCommentId(noticeCommentId);
//    }
//
//    public int getReplyCountByMemberPublicId(UUID publicId) {
//        return noticeCommentQueryRepository.getReplyCountByPublicId(publicId);
//    }
//}
