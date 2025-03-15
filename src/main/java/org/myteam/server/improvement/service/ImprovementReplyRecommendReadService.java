//package org.myteam.server.improvement.service;
//
//import lombok.RequiredArgsConstructor;
//import org.myteam.server.global.exception.ErrorCode;
//import org.myteam.server.global.exception.PlayHiveException;
//import org.myteam.server.improvement.domain.ImprovementReply;
//import org.myteam.server.improvement.repository.ImprovementReplyRecommendRepository;
//import org.myteam.server.improvement.repository.ImprovementReplyRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class ImprovementReplyRecommendReadService {
//
//    private final ImprovementReplyRecommendRepository improvementReplyRecommendRepository;
//    private final ImprovementReplyRepository improvementReplyRepository;
//
//    public void confirmExistImprovementReply(Long improvementReplyId, UUID publicId) {
//        improvementReplyRecommendRepository.findByImprovementReplyIdAndMemberPublicId(improvementReplyId, publicId)
//                .ifPresent(member -> {
//                    throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND_REPLY);
//                });
//    }
//
//    public boolean isAlreadyRecommended(Long improvementReplyId, UUID publicId) {
//        if (!improvementReplyRecommendRepository.findByImprovementReplyIdAndMemberPublicId(improvementReplyId, publicId)
//                .isPresent()) {
//            throw new PlayHiveException(ErrorCode.NO_MEMBER_RECOMMEND_RECORD);
//        }
//        return true;
//    }
//
//    public boolean isRecommended(Long improvementReplyId, UUID publicId) {
//        return improvementReplyRecommendRepository.findByImprovementReplyIdAndMemberPublicId(improvementReplyId, publicId).isPresent();
//    }
//
//    public ImprovementReply findById(Long improvementReplyId) {
//        return improvementReplyRepository.findById(improvementReplyId)
//                .orElseThrow(() -> new PlayHiveException(ErrorCode.IMPROVEMENT_REPLY_NOT_FOUND));
//    }
//}
