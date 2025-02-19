package org.myteam.server.board.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.myteam.server.board.repository.BoardCommentRecommendRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardCommentRecommendReadService {

    private final BoardCommentRecommendRepository boardCommentRecommendRepository;

    public void confirmExistBoardRecommend(Long boardCommentId, UUID publicId) {
        boardCommentRecommendRepository.findByBoardCommentIdAndMemberPublicId(boardCommentId, publicId)
                .ifPresent(member -> {
                    throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND_COMMENT);
                });
    }

    public boolean isAlreadyRecommended(Long boardCommentId, UUID publicId) {
        if (!boardCommentRecommendRepository.findByBoardCommentIdAndMemberPublicId(boardCommentId, publicId)
                .isPresent()) {
            throw new PlayHiveException(ErrorCode.NO_MEMBER_RECOMMEND_RECORD);
        }
        return true;
    }
}