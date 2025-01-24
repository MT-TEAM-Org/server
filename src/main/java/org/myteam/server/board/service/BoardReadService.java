package org.myteam.server.board.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.entity.Board;
import org.myteam.server.board.repository.BoardRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardReadService {

    private final BoardRepository boardRepository;

    public Board findById(long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_NOT_FOUND));
    }
}