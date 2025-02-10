package org.myteam.server.board.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.board.repository.BoardCountRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardCountReadService {

    private final BoardCountRepository boardCountRepository;

    public BoardCount findByBoardId(Long boardId) {
        return boardCountRepository.findByBoardId(boardId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_RECOMMEND_NOT_FOUND));
    }
}