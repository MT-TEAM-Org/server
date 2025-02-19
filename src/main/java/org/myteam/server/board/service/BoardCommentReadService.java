package org.myteam.server.board.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.BoardComment;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.dto.reponse.BoardCommentListResponse;
import org.myteam.server.board.dto.reponse.BoardCommentResponse;
import org.myteam.server.board.repository.BoardCommentQueryRepository;
import org.myteam.server.board.repository.BoardCommentRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardCommentReadService {

    private final BoardCommentRepository boardCommentRepository;
    private final BoardCommentQueryRepository boardCommentQueryRepository;
    private final MemberRepository memberRepository;

    private final BoardCommentRecommendReadService boardCommentRecommendReadService;

    public BoardComment findById(Long boardCommentId) {
        return boardCommentRepository.findById(boardCommentId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BOARD_COMMENT_NOT_FOUND));
    }

    public BoardCommentListResponse findByBoardId(Long boardId, BoardOrderType orderType,
                                                  CustomUserDetails userDetails) {

        List<BoardCommentResponse> list = boardCommentQueryRepository.getBoardCommentList(
                boardId,
                orderType,
                userDetails
        );

        return BoardCommentListResponse.createResponse(list);
    }

    public BoardCommentResponse findByIdWithReply(Long boardCommentId, CustomUserDetails userDetails) {
        BoardComment boardComment = findById(boardCommentId);

        boolean boardCommentIsRecommended = false;

        if (userDetails != null) {
            UUID loginUser = memberRepository.findByPublicId(userDetails.getPublicId()).get().getPublicId();
            boardCommentIsRecommended = boardCommentRecommendReadService.isRecommended(boardComment.getId(), loginUser);
        }

        BoardCommentResponse response = BoardCommentResponse.createResponse(boardComment, boardComment.getMember(),
                boardCommentIsRecommended);

        response.setBoardReplyList(boardCommentQueryRepository.getRepliesForComments(boardCommentId, userDetails));

        return response;
    }
}