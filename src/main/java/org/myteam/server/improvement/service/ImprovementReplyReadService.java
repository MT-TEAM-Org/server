package org.myteam.server.improvement.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.improvement.domain.ImprovementReply;
import org.myteam.server.improvement.repository.ImprovementCommentQueryRepository;
import org.myteam.server.improvement.repository.ImprovementReplyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImprovementReplyReadService {

    private final ImprovementReplyRepository improvementReplyRepository;
    private final ImprovementCommentQueryRepository improvementCommentQueryRepository;

    public ImprovementReply findById(Long improvementReplyId) {
        return improvementReplyRepository.findById(improvementReplyId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.IMPROVEMENT_REPLY_NOT_FOUND));
    }

    public List<ImprovementReply> findByImprovementCommentId(Long improvementCommentId) {
        return improvementReplyRepository.findByImprovementCommentId(improvementCommentId);
    }

    public int getReplyCountByMemberPublicId(UUID publicId) {
        return improvementCommentQueryRepository.getReplyCountByPublicId(publicId);
    }
}
