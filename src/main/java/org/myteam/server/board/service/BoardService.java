package org.myteam.server.board.service;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.controller.reponse.BoardResponse;
import org.myteam.server.board.dto.BoardSaveRequest;
import org.myteam.server.board.entity.Board;
import org.myteam.server.board.entity.BoardCount;
import org.myteam.server.board.entity.Category;
import org.myteam.server.board.repository.BoardCountRepository;
import org.myteam.server.board.repository.BoardRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardCountRepository boardCountRepository;

    private final BoardReadService boardReadService;
    private final MemberReadService memberReadService;
    private final CategoryReadService categoryReadService;

    /**
     * 게시글 작성
     */
    @Transactional
    public BoardResponse saveBoard(final BoardSaveRequest request, final CustomUserDetails userDetails,
                                   final String clientIP) {

        Member member = memberReadService.findById(userDetails.getPublicId());
        Category category = categoryReadService.findById(request.getCategoryId());

        Board board = makeBoard(category, member, clientIP, request);

        return new BoardResponse(board);
    }

    /**
     * 게시글, 게시글 카운트 생성
     */
    private Board makeBoard(final Category category, final Member member, final String clientIP,
                            final BoardSaveRequest request) {

        final Board board = Board.builder()
                .category(category)
                .member(member)
                .createdIp(clientIP)
                .title(request.getTitle())
                .content(request.getContent())
                .link(request.getLink())
                .build();
        boardRepository.save(board);

        BoardCount boardCount = BoardCount.createBoardCount(board);
        boardCountRepository.save(boardCount);

        return board;
    }

    /**
     * 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public BoardResponse getBoard(final Long boardId) {

        Board board = boardReadService.findById(boardId);

        return new BoardResponse(board);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deleteBoard(final Long boardId, final CustomUserDetails userDetails) {

        Member member = memberReadService.findById(userDetails.getPublicId());
        Board board = boardReadService.findById(boardId);

        verifyBoardAuthor(board, member);

        boardCountRepository.deleteByBoardId(board.getId());
        boardRepository.delete(board);
    }

    /**
     * 게시글 수정
     */
    public BoardResponse updateBoard(final BoardSaveRequest request, final CustomUserDetails userDetails,
                                     final Long boardId) {

        Board board = boardReadService.findById(boardId);
        Member member = memberReadService.findById(userDetails.getPublicId());

        verifyBoardAuthor(board, member);

        Category category = categoryReadService.findById(request.getCategoryId());

        board.updateBoard(request, category);
        boardRepository.save(board);

        return new BoardResponse(board);
    }

    /**
     * 작성자와 일치 하는지 검사 (어드민 수정/삭제 허용)
     */
    private void verifyBoardAuthor(final Board board, final Member member) {
        if (!board.getMember().getId().equals(member.getId())) {
            if (!member.isAdmin()) {
                throw new PlayHiveException(ErrorCode.POST_AUTHOR_MISMATCH);
            }
        }
    }
}
