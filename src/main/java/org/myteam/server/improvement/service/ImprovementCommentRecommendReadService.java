//package org.myteam.server.improvement.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.myteam.server.global.exception.ErrorCode;
//import org.myteam.server.global.exception.PlayHiveException;
//import org.myteam.server.improvement.domain.ImprovementComment;
//import org.myteam.server.improvement.repository.ImprovementCommentRecommendRepository;
//import org.myteam.server.improvement.repository.ImprovementCommentRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.UUID;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class ImprovementCommentRecommendReadService {
//
//    private final ImprovementCommentRecommendRepository improvementCommentRecommendRepository;
//    private final ImprovementCommentRepository improvementCommentRepository;
//
//    public void confirmExistImprovementCommentRecommend(Long improvementCommentId, UUID memberPublicId) {
//        improvementCommentRecommendRepository
//                .findByImprovementCommentIdAndMemberPublicId(improvementCommentId, memberPublicId)
//                .ifPresent(member -> {
//                    throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND_COMMENT);
//                });
//    }
//
//    public boolean isAlreadyRecommended(Long improvementCommentId, UUID publicId) {
//        if (!improvementCommentRecommendRepository
//                .findByImprovementCommentIdAndMemberPublicId(improvementCommentId, publicId)
//                .isPresent()) {
//            throw new PlayHiveException(ErrorCode.NO_MEMBER_RECOMMEND_RECORD);
//        }
//        return true;
//    }
//
//    public boolean isRecommended(Long improvementCommentId, UUID publicId) {
//        return improvementCommentRecommendRepository
//                .findByImprovementCommentIdAndMemberPublicId(improvementCommentId, publicId)
//                .isPresent();
//    }
//
//    public ImprovementComment findById(Long improvementCommentId) {
//        return improvementCommentRepository.findById(improvementCommentId)
//                .orElseThrow(() -> new PlayHiveException(ErrorCode.NOTICE_COMMENT_NOT_FOUND));
//    }
//}
