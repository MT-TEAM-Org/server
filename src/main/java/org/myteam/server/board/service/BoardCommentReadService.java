package org.myteam.server.board.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.BoardComment;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.dto.reponse.BoardCommentListResponse;
import org.myteam.server.board.dto.reponse.BoardCommentResponse;
import org.myteam.server.board.repository.BoardCommentQueryRepository;
import org.myteam.server.board.repository.BoardCommentRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardCommentReadService {

    private final BoardCommentRepository boardCommentRepository;
    private final BoardCommentQueryRepository boardCommentQueryRepository;

    public BoardComment findById(Long boardCommentId) {
        return boardCommentRepository.findById(boardCommentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_COMMENT_NOT_FOUND));
    }

    public BoardCommentListResponse findByBoardId(Long boardId, BoardOrderType orderType) {

        List<BoardCommentResponse> list = boardCommentQueryRepository.getBoardCommentList(
                boardId,
                orderType
        );

        return BoardCommentListResponse.createResponse(list);
    }
}