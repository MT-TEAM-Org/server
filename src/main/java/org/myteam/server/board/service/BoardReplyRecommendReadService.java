package org.myteam.server.board.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.BoardReply;
import org.myteam.server.board.repository.BoardReplyLockRepository;
import org.myteam.server.board.repository.BoardReplyRecommendRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardReplyRecommendReadService {

    private final BoardReplyRecommendRepository boardReplyRecommendRepository;
    private final BoardReplyLockRepository boardReplyLockRepository;

    public void confirmExistBoardReply(Long boardReplyId, UUID publicId) {
        boardReplyRecommendRepository.findByBoardReplyIdAndMemberPublicId(boardReplyId, publicId)
                .ifPresent(member -> {
                    throw new PlayHiveException(ErrorCode.ALREADY_MEMBER_RECOMMEND_REPLY);
                });
    }

    public boolean isAlreadyRecommended(Long boardReplyId, UUID publicId) {
        if (!boardReplyRecommendRepository.findByBoardReplyIdAndMemberPublicId(boardReplyId, publicId)
                .isPresent()) {
            throw new PlayHiveException(ErrorCode.NO_MEMBER_RECOMMEND_RECORD);
        }
        return true;
    }

    public boolean isRecommended(Long boardReplyId, UUID publicId) {
        return boardReplyRecommendRepository.findByBoardReplyIdAndMemberPublicId(boardReplyId, publicId).isPresent();
    }

    public BoardReply findByIdLock(Long boardReplyId) {
        return boardReplyLockRepository.findById(boardReplyId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_REPLY_RECOMMEND_NOT_FOUND));
    }
}