package org.myteam.server.board.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.myteam.server.board.repository.BoardRecommendRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardRecommendReadService {

    private final BoardRecommendRepository boardRecommendRepository;

    public boolean isAlreadyRecommended(Long boardId, UUID publicId) {
        if (!boardRecommendRepository.findByBoardIdAndMemberPublicId(boardId, publicId).isPresent()) {
            throw new PlayHiveException(ErrorCode.NO_MEMBER_RECOMMEND_RECORD);
        }
        return true;
    }

    public boolean isRecommended(Long boardId, UUID publicId) {
        return boardRecommendRepository.findByBoardIdAndMemberPublicId(boardId, publicId).isPresent();
    }
}