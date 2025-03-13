//package org.myteam.server.notice.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.myteam.server.global.exception.ErrorCode;
//import org.myteam.server.global.exception.PlayHiveException;
//import org.myteam.server.notice.repository.NoticeCommentRecommendRepository;
//import org.myteam.server.notice.repository.NoticeCommentRepository;
//import org.myteam.server.notice.domain.NoticeComment;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.UUID;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class NoticeCommentRecommendReadService {
//
//    private final NoticeCommentRecommendRepository noticeCommentRecommendRepository;
//    private final NoticeCommentRepository noticeCommentRepository;
//
//    public void confirmExistNoticeCommentRecommend(Long noticeCommentId, UUID memberPublicId) {
//        noticeCommentRecommendRepository.findByNoticeCommentIdAndMemberPublicId(noticeCommentId, memberPublicId)
//                .ifPresent(member -> {
//                    throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND_COMMENT);
//                });
//    }
//
//    public boolean isAlreadyRecommended(Long noticeCommentId, UUID publicId) {
//        if (!noticeCommentRecommendRepository.findByNoticeCommentIdAndMemberPublicId(noticeCommentId, publicId)
//                .isPresent()) {
//            throw new PlayHiveException(ErrorCode.NO_MEMBER_RECOMMEND_RECORD);
//        }
//        return true;
//    }
//
//    public boolean isRecommended(Long noticeCommentId, UUID publicId) {
//        return noticeCommentRecommendRepository.findByNoticeCommentIdAndMemberPublicId(noticeCommentId, publicId)
//                .isPresent();
//    }
//
//    public NoticeComment findById(Long noticeCommentId) {
//        return noticeCommentRepository.findById(noticeCommentId)
//                .orElseThrow(() -> new PlayHiveException(ErrorCode.NOTICE_COMMENT_NOT_FOUND));
//    }
//}
