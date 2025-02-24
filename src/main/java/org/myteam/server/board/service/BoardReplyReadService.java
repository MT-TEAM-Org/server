package org.myteam.server.board.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.BoardReply;
import org.myteam.server.board.repository.BoardReplyRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardReplyReadService {

    private final BoardReplyRepository boardReplyRepository;

    public BoardReply findById(Long boardReplyId) {
        return boardReplyRepository.findById(boardReplyId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_REPLY_NOT_FOUND));
    }

    public List<BoardReply> findByBoardCommentId(Long boardCommentId) {
        return boardReplyRepository.findByBoardCommentId(boardCommentId);
    }

    public List<BoardReply> findAllByBoardCommentId(Long boardCommentId) {
        return boardReplyRepository.findAllByBoardCommentId(boardCommentId);
    }
}