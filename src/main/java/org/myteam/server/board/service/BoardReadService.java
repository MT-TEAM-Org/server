package org.myteam.server.board.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.dto.reponse.BoardDto;
import org.myteam.server.board.dto.reponse.BoardListResponse;
import org.myteam.server.board.dto.request.BoardServiceRequest;
import org.myteam.server.board.repository.BoardQueryRepository;
import org.myteam.server.board.repository.BoardRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.mypage.dto.request.MyBoardServiceRequest;
import org.myteam.server.notice.Repository.NoticeQueryRepository;
import org.myteam.server.notice.dto.response.NoticeResponse.NoticeDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardReadService {

    private final BoardRepository boardRepository;
    private final BoardQueryRepository boardQueryRepository;
    private final NoticeQueryRepository noticeQueryRepository;

    public Board findById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_NOT_FOUND));
    }

    public BoardListResponse getBoardList(BoardServiceRequest boardServiceRequest) {
        List<NoticeDto> noticeList = noticeQueryRepository.getFixNotice();
        Page<BoardDto> boardPagingList = boardQueryRepository.getBoardList(
                boardServiceRequest.getBoardType(),
                boardServiceRequest.getCategoryType(),
                boardServiceRequest.getOrderType(),
                boardServiceRequest.getSearchType(),
                boardServiceRequest.getSearch(),
                boardServiceRequest.toPageable()
        );
        return BoardListResponse.createResponse(noticeList, PageCustomResponse.of(boardPagingList));
    }

    public BoardListResponse getMyBoardList(MyBoardServiceRequest myBoardServiceRequest, UUID publicId) {
        log.info("내 게시글 조회: {} orderType: {}  searchType: {}, search: {}, page: {}, size: {}",
                publicId,
                myBoardServiceRequest.getOrderType(),
                myBoardServiceRequest.getSearchType(),
                myBoardServiceRequest.getSearch(),
                myBoardServiceRequest.getPage(),
                myBoardServiceRequest.getSize()
        );

        List<NoticeDto> noticeList = noticeQueryRepository.getFixNotice();

        Page<BoardDto> myBoardList = boardQueryRepository.getMyBoardList(
                myBoardServiceRequest.getOrderType(),
                myBoardServiceRequest.getSearchType(),
                myBoardServiceRequest.getSearch(),
                myBoardServiceRequest.toPageable(),
                publicId
        );
        return BoardListResponse.createResponse(noticeList, PageCustomResponse.of(myBoardList));
    }

    public int getMyBoardListCount(UUID memberPublicId) {
        return boardQueryRepository.getMyBoard(memberPublicId);
    }
}