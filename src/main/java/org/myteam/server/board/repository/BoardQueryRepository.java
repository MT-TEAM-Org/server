package org.myteam.server.board.repository;

import static java.util.Optional.ofNullable;
import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.board.domain.QBoardComment.boardComment;
import static org.myteam.server.board.domain.QBoardCount.boardCount;
import static org.myteam.server.member.entity.QMember.member;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.board.domain.BoardType;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.dto.reponse.BoardCommentSearchDto;
import org.myteam.server.board.dto.reponse.BoardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BoardQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 게시글 목록 조회
     */
    public Page<BoardDto> getBoardList(BoardType boardType, CategoryType categoryType,
                                       BoardOrderType orderType,
                                       BoardSearchType searchType, String search, Pageable pageable) {

        List<BoardDto> content = queryFactory
                .select(Projections.constructor(BoardDto.class,
                        board.boardType,
                        board.categoryType,
                        board.id,
                        board.id.in(getHotBoardList()).as("isHot"),
                        board.title,
                        board.createdIp,
                        board.thumbnail,
                        member.publicId,
                        member.nickname,
                        boardCount.commentCount,
                        boardCount.recommendCount,
                        board.createDate,
                        board.lastModifiedDate
                ))
                .from(board)
                .join(boardCount).on(boardCount.board.id.eq(board.id))
                .join(member).on(member.eq(board.member))
                .fetchJoin()
                .where(isBoardTypeEqualTo(boardType), isCategoryEqualTo(categoryType),
                        isSearchTypeLikeTo(searchType, search))
                .orderBy(isOrderByEqualToOrderCategory(orderType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = getTotalBoardCount(boardType, categoryType, searchType, search);

        // searchType이 COMMENT일 경우, 댓글 데이터 추가
        if (searchType == BoardSearchType.COMMENT && search != null) {
            content.forEach(boardDto -> {

                BoardCommentSearchDto commentSearch = getSearchBoardComment(boardDto.getId(), search);

                if (commentSearch != null) {
                    boardDto.setBoardCommentSearchDto(commentSearch);
                }
            });
        }

        return new PageImpl<>(content, pageable, total);
    }

    private BoardCommentSearchDto getSearchBoardComment(Long boardId, String search) {
        return queryFactory
                .select(Projections.fields(BoardCommentSearchDto.class,
                        boardComment.id.as("boardCommentId"),
                        boardComment.comment
                ))
                .from(boardComment)
                .where(boardComment.board.id.eq(boardId)
                        .and(boardComment.comment.like("%" + search + "%")))
                .orderBy(boardComment.createDate.desc(), boardComment.comment.asc())
                .fetchFirst();
    }


    private BooleanExpression isSearchTypeLikeTo(BoardSearchType searchType, String search) {
        if (searchType == null || search == null || search.isEmpty()) {
            return null;
        }

        return switch (searchType) {
            case TITLE -> board.title.like("%" + search + "%");
            case CONTENT -> board.content.like("%" + search + "%");
            case TITLE_CONTENT -> board.title.like("%" + search + "%")
                    .or(board.content.like("%" + search + "%"));
            case NICKNAME -> board.member.nickname.like("%" + search + "%");
            default -> null;
        };
    }

    private long getTotalBoardCount(BoardType boardType, CategoryType categoryType, BoardSearchType searchType,
                                    String search) {
        return ofNullable(
                queryFactory
                        .select(board.count())
                        .from(board)
                        .where(isBoardTypeEqualTo(boardType), isCategoryEqualTo(categoryType),
                                isSearchTypeLikeTo(searchType, search))
                        .fetchOne()
        ).orElse(0L);
    }

    private OrderSpecifier<?>[] isOrderByEqualToOrderCategory(BoardOrderType orderType) {
        // default 최신순
        BoardOrderType boardOrderType = Optional.ofNullable(orderType).orElse(BoardOrderType.CREATE);
        return switch (boardOrderType) {
            case CREATE -> new OrderSpecifier<?>[]{board.createDate.desc(), board.title.asc(), board.id.desc()};
            case RECOMMEND -> new OrderSpecifier<?>[]{boardCount.recommendCount.desc(),
                    boardCount.commentCount.add(boardCount.viewCount).desc(), board.title.asc(), board.id.desc()};
            case COMMENT -> new OrderSpecifier<?>[]{boardCount.commentCount.desc(), board.title.asc(), board.id.desc()};
        };
    }

    private BooleanExpression isCategoryEqualTo(CategoryType categoryType) {
        return categoryType != null ? board.categoryType.eq(categoryType) : null;
    }

    private BooleanExpression isBoardTypeEqualTo(BoardType boardType) {
        return boardType != null ? board.boardType.eq(boardType) : null;
    }

    /**
     * 내가 쓴 게시글 목록 조회
     */
    public Page<BoardDto> getMyBoardList(BoardOrderType orderType, BoardSearchType searchType, String search,
                                         Pageable pageable, UUID publicId) {

        List<BoardDto> content = queryFactory
                .select(Projections.constructor(BoardDto.class,
                        board.boardType,
                        board.categoryType,
                        board.id,
                        board.id.in(getHotBoardList()).as("isHot"),
                        board.title,
                        board.createdIp,
                        board.thumbnail,
                        member.publicId,
                        member.nickname,
                        boardCount.commentCount,
                        boardCount.recommendCount,
                        board.createDate,
                        board.lastModifiedDate
                ))
                .from(board)
                .join(boardCount).on(boardCount.board.id.eq(board.id))
                .join(member).on(member.eq(board.member))
                .fetchJoin()
                .where(member.publicId.eq(publicId), isSearchTypeLikeTo(searchType, search))
                .orderBy(isOrderByEqualToOrderCategory(orderType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = getTotalMyBoardCount(searchType, search, publicId);

        log.info("검색 완료 total: {}", total);

        return new PageImpl<>(content, pageable, total);
    }

    public int getMyBoard(UUID memberPublicId) {
        log.info("publicID: {} 포스트 수 조회", memberPublicId);
        return queryFactory
                .select(board.count())
                .from(board)
                .where(board.member.publicId.eq(memberPublicId))
                .fetchOne()
                .intValue();
    }

    /**
     * 내가 쓴 게시글 총 개수
     */
    private long getTotalMyBoardCount(BoardSearchType searchType, String search, UUID publicId) {
        return ofNullable(
                queryFactory
                        .select(board.count())
                        .from(board)
                        .join(member).on(member.eq(board.member))
                        .where(member.publicId.eq(publicId), isSearchTypeLikeTo(searchType, search))
                        .fetchOne()
        ).orElse(0L);
    }

    /**
     * 핫 게시글 ID 목록 조회
     */
    private List<Long> getHotBoardList() {
        // 전체 게시글 기준 추천순 내림차순 -> 조회수 + 댓글수 내림차순 -> 제목 오름차순 -> id 오름차순
        return queryFactory
                .select(board.id)
                .from(board)
                .join(boardCount).on(boardCount.board.id.eq(board.id))
                .orderBy(
                        boardCount.recommendCount.desc(),
                        boardCount.viewCount.add(boardCount.commentCount).desc(),
                        board.title.asc(), board.id.asc()
                )
                .limit(10)
                .fetch();
    }
}