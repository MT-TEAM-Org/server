//package org.myteam.server.notice.service;
//
//import lombok.RequiredArgsConstructor;
//import org.myteam.server.global.exception.ErrorCode;
//import org.myteam.server.global.exception.PlayHiveException;
//import org.myteam.server.notice.repository.NoticeReplyRecommendRepository;
//import org.myteam.server.notice.repository.NoticeReplyRepository;
//import org.myteam.server.notice.domain.NoticeReply;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class NoticeReplyRecommendReadService {
//
//    private final NoticeReplyRecommendRepository noticeReplyRecommendRepository;
//    private final NoticeReplyRepository noticeReplyRepository;
//
//    public void confirmExistNoticeReply(Long noticeReplyId, UUID publicId) {
//        noticeReplyRecommendRepository.findByNoticeReplyIdAndMemberPublicId(noticeReplyId, publicId)
//                .ifPresent(member -> {
//                    throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND_REPLY);
//                });
//    }
//
//    public boolean isAlreadyRecommended(Long noticeReplyId, UUID publicId) {
//        if (!noticeReplyRecommendRepository.findByNoticeReplyIdAndMemberPublicId(noticeReplyId, publicId)
//                .isPresent()) {
//            throw new PlayHiveException(ErrorCode.NO_MEMBER_RECOMMEND_RECORD);
//        }
//        return true;
//    }
//
//    public boolean isRecommended(Long noticeReplyId, UUID publicId) {
//        return noticeReplyRecommendRepository.findByNoticeReplyIdAndMemberPublicId(noticeReplyId, publicId).isPresent();
//    }
//
//    public NoticeReply findById(Long noticeReplyId) {
//        return noticeReplyRepository.findById(noticeReplyId)
//                .orElseThrow(() -> new PlayHiveException(ErrorCode.NOTICE_REPLY_NOT_FOUND));
//    }
//}
