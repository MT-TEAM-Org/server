package org.myteam.server.board.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.aop.CountView;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.BoardCount;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.dto.reponse.BoardResponse;
import org.myteam.server.board.dto.request.BoardRequest;
import org.myteam.server.board.repository.BoardCountRepository;
import org.myteam.server.board.repository.BoardQueryRepository;
import org.myteam.server.board.repository.BoardRecommendRepository;
import org.myteam.server.board.repository.BoardRepository;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.service.CommentService;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.RedisCountService;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.report.domain.DomainType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardCountRepository boardCountRepository;
    private final BoardRecommendRepository boardRecommendRepository;
    private final MemberRepository memberRepository;
    private final BoardQueryRepository boardQueryRepository;

    private final SecurityReadService securityReadService;
    private final BoardReadService boardReadService;
    private final BoardCountReadService boardCountReadService;
    private final MemberReadService memberReadService;
    private final BoardRecommendReadService boardRecommendReadService;
    private final CommentService commentService;
    private final RedisCountService redisCountService;

    /**
     * 게시글 작성
     */
    @Transactional
    public BoardResponse saveBoard(final BoardRequest request, final String clientIP) {
        log.info("save board 실행");

        UUID loginUser = securityReadService.getMember().getPublicId();
        Member member = memberReadService.findById(loginUser);

        log.info("user: {} 게시판 업로드 요청", loginUser);

        verifyBoardTypeAndCategoryType(request.getBoardType(), request.getCategoryType());

        Board board = makeBoard(member, clientIP, request);

        CommonCountDto commonCountDto = redisCountService.getCommonCount(DomainType.BOARD,
                board.getId());

        boolean isRecommended = boardRecommendReadService.isRecommended(board.getId(), loginUser);

        // 이전글/다음글 ID 조회 (게시판 타입(BASEBALL, FOOTBALL...), 카테고리 타입(FREE,QUESTION...) 기준으로 조회)
        Long previousId = boardQueryRepository.findPreviousBoardId(board.getId(), board.getBoardType(),
                board.getCategoryType());
        Long nextId = boardQueryRepository.findNextBoardId(board.getId(), board.getBoardType(),
                board.getCategoryType());

        log.info("게시판 생성: {}", loginUser);
        return BoardResponse.createResponse(board, isRecommended, previousId, nextId, commonCountDto);
    }

    /**
     * 게시판에 맞는 카테고리를 선택했는지 검사 ex) 전적 인증, 플레이팁은 e-sport 게시판에서만 사용
     */
    private void verifyBoardTypeAndCategoryType(Category category, CategoryType categoryType) {
        if (!category.isEsports()) {
            categoryType.confirmEsports();
        }
    }

    /**
     * 게시글, 게시글 카운트 생성
     */
    private Board makeBoard(final Member member, final String clientIP,
                            final BoardRequest request) {

        final Board board = Board.builder()
                .member(member)
                .boardType(request.getBoardType())
                .categoryType(request.getCategoryType())
                .createdIp(clientIP)
                .title(request.getTitle())
                .content(request.getContent())
                .link(request.getLink())
                .thumbnail(request.getThumbnail())
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
    @CountView(domain = DomainType.BOARD, idParam = "boardId")
    public BoardResponse getBoard(final Long boardId) {
        UUID loginUser = securityReadService.getAuthenticatedPublicId();
        Board board = boardReadService.findById(boardId);

        CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.BOARD,
                board.getId(), null);

        boolean isRecommended = false;
        if (loginUser != null) {
            isRecommended = boardRecommendReadService.isRecommended(board.getId(), loginUser);
        }

        // 이전글/다음글 ID 조회 (게시판 타입(BASEBALL, FOOTBALL...), 카테고리 타입(FREE,QUESTION...) 기준으로 조회)
        Long previousId = boardQueryRepository.findPreviousBoardId(boardId, board.getBoardType(),
                board.getCategoryType());
        Long nextId = boardQueryRepository.findNextBoardId(boardId, board.getBoardType(), board.getCategoryType());

        return BoardResponse.createResponse(board, isRecommended, previousId, nextId, commonCountDto);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public BoardResponse updateBoard(final BoardRequest request, final Long boardId) {

        UUID loginUser = securityReadService.getMember().getPublicId();

        Board board = boardReadService.findById(boardId);
        Member member = memberReadService.findById(loginUser);

        verifyBoardAuthor(board, member);
        verifyBoardTypeAndCategoryType(request.getBoardType(), request.getCategoryType());

        board.updateBoard(request);
        boardRepository.save(board);

        CommonCountDto commonCountDto = redisCountService.getCommonCount(DomainType.BOARD, board.getId());

        boolean isRecommended = boardRecommendReadService.isRecommended(board.getId(), loginUser);

        // 이전글/다음글 ID 조회 (게시판 타입(BASEBALL, FOOTBALL...), 카테고리 타입(FREE,QUESTION...) 기준으로 조회)
        Long previousId = boardQueryRepository.findPreviousBoardId(board.getId(), board.getBoardType(),
                board.getCategoryType());
        Long nextId = boardQueryRepository.findNextBoardId(board.getId(), board.getBoardType(),
                board.getCategoryType());

        return BoardResponse.createResponse(board, isRecommended, previousId, nextId, commonCountDto);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deleteBoard(final Long boardId) {

        UUID loginUser = securityReadService.getMember().getPublicId();

        Member member = memberReadService.findById(loginUser);
        Board board = boardReadService.findById(boardId);

        verifyBoardAuthor(board, member);

        // 게시글 댓글, 댓글 추천 삭제
        commentService.deleteCommentByPost(CommentType.BOARD, boardId);
        //게시글 추천 삭제
        boardRecommendRepository.deleteAllByBoardId(board.getId());
        // 조회수 삭제
        redisCountService.removeCount(DomainType.BOARD, boardId);
        // 게시글 카운트 삭제
        boardCountRepository.deleteByBoardId(board.getId());
        // 게시글 삭제
        boardRepository.delete(board);
    }

    /**
     * 작성자와 일치 하는지 검사 (어드민도 수정/삭제 허용)
     */
    private void verifyBoardAuthor(Board board, Member member) {
        if (!board.isAuthor(member) && !member.isAdmin()) {
            throw new PlayHiveException(ErrorCode.POST_AUTHOR_MISMATCH);
        }
    }
}