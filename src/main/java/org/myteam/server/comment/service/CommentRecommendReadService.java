package org.myteam.server.comment.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.myteam.server.comment.repository.CommentRecommendRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentRecommendReadService {

    private final CommentRecommendRepository boardCommentRecommendRepository;

    public void confirmExistRecommend(Long boardCommentId, UUID publicId) {
        boardCommentRecommendRepository.findByCommentIdAndMemberPublicId(boardCommentId, publicId)
                .ifPresent(member -> {
                    throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND_COMMENT);
                });
    }

    public boolean isAlreadyRecommended(Long boardCommentId, UUID publicId) {
        if (!boardCommentRecommendRepository.findByCommentIdAndMemberPublicId(boardCommentId, publicId)
                .isPresent()) {
            throw new PlayHiveException(ErrorCode.NO_MEMBER_RECOMMEND_RECORD);
        }
        return true;
    }

    public boolean isRecommended(Long boardCommentId, UUID publicId) {
        return boardCommentRecommendRepository.findByCommentIdAndMemberPublicId(boardCommentId, publicId)
                .isPresent();
    }
}