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

    private final CommentRecommendRepository commentRecommendRepository;

    /**
     * 이미 추천한 상태인지 검사 (추천 시)
     */
    public void confirmExistRecommend(Long commentId, UUID publicId) {
        commentRecommendRepository
                .findByCommentIdAndMemberPublicId(commentId, publicId)
                .ifPresent(member -> {
                    throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND_COMMENT);
                });
    }

    /**
     * 추천이 되어있는 상태인지 검사 (추천 삭제 시)
     */
    public boolean isAlreadyRecommended(Long commentId, UUID publicId) {
        if (!commentRecommendRepository
                .findByCommentIdAndMemberPublicId(commentId, publicId).isPresent()) {
            throw new PlayHiveException(ErrorCode.NO_MEMBER_RECOMMEND_RECORD);
        }
        return true;
    }

    /**
     * 추천 여부 조회
     */
    public boolean isRecommended(Long commentId, UUID publicId) {
        return commentRecommendRepository
                .findByCommentIdAndMemberPublicId(commentId, publicId).isPresent();
    }
}