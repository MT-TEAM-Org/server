package org.myteam.server.board.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.board.dto.reponse.BoardDto;
import org.myteam.server.board.dto.reponse.BoardListResponse;
import org.myteam.server.board.dto.request.BoardServiceRequest;
import org.myteam.server.board.repository.BoardCountRepository;
import org.myteam.server.board.repository.BoardQueryRepository;
import org.myteam.server.board.repository.BoardRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardReadService {

    private final BoardRepository boardRepository;
    private final BoardCountRepository boardCountRepository;
    private final BoardQueryRepository boardQueryRepository;

    public Board boardFindById(long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_NOT_FOUND));
    }

    public BoardCount boardCountFindById(long boardId) {
        return boardCountRepository.findById(boardId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_NOT_FOUND));
    }

    public BoardListResponse getBoardList(BoardServiceRequest boardServiceRequest) {
        Page<BoardDto> boardPagingList = boardQueryRepository.getBoardList(
                boardServiceRequest.getBoardType(),
                boardServiceRequest.getCategoryType(),
                boardServiceRequest.getOrderType(),
                boardServiceRequest.toPageable()
        );
        return BoardListResponse.createResponse(PageCustomResponse.of(boardPagingList));
    }
}