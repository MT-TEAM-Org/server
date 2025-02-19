package org.myteam.server.board.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
}