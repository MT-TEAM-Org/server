package org.myteam.server.comment.repository;

import static java.util.Optional.ofNullable;
import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.board.domain.QBoardCount.boardCount;
import static org.myteam.server.comment.domain.QBoardComment.boardComment;
import static org.myteam.server.comment.domain.QComment.comment1;
import static org.myteam.server.comment.domain.QImprovementComment.improvementComment;
import static org.myteam.server.comment.domain.QInquiryComment.inquiryComment;
import static org.myteam.server.comment.domain.QNewsComment.newsComment;
import static org.myteam.server.comment.domain.QNoticeComment.noticeComment;
import static org.myteam.server.improvement.domain.QImprovement.improvement;
import static org.myteam.server.inquiry.domain.QInquiry.inquiry;
import static org.myteam.server.member.entity.QMember.member;
import static org.myteam.server.news.news.domain.QNews.news;
import static org.myteam.server.news.newsCount.domain.QNewsCount.newsCount;
import static org.myteam.server.notice.domain.QNotice.notice;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.domain.QBoardComment;
import org.myteam.server.comment.domain.QComment;
import org.myteam.server.comment.domain.QImprovementComment;
import org.myteam.server.comment.domain.QInquiryComment;
import org.myteam.server.comment.domain.QMatchComment;
import org.myteam.server.comment.domain.QNewsComment;
import org.myteam.server.comment.domain.QNoticeComment;
import org.myteam.server.comment.dto.response.CommentResponse.BestCommentResponse;
import org.myteam.server.comment.dto.response.CommentResponse.CommentSaveResponse;
import org.myteam.server.comment.service.CommentRecommendReadService;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.page.util.CustomPageImpl;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.entity.QMember;
import org.myteam.server.mypage.dto.response.MyCommentDto;
import org.myteam.server.mypage.dto.response.PostResponse;
import org.myteam.server.util.ClientUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final CommentRepository commentRepository;
    private final CommentRecommendReadService commentRecommendReadService;

    /**
     * 대댓글 목록 조회
     */
    public List<Comment> findRepliesWithImages(Long parentId) {
        return queryFactory
                .selectFrom(comment1)
                .where(comment1.parent.id.eq(parentId)
                        .and(comment1.imageUrl.isNotNull()))
                .fetch();
    }

    /**
     * 댓글 삭제
     */
    public int deleteReply(Long parentId) {
        // 대댓글 삭제
        long deletedCount = queryFactory
                .delete(comment1)
                .where(comment1.parent.id.eq(parentId))
                .execute();

        // 부모 댓글 삭제
        commentRepository.deleteById(parentId);

        // 삭제 갯수 반환
        return (int) deletedCount + 1;
    }

    public List<Comment> getCommentList(CommentType type, Long contentId) {
        QMember mentionedMember = new QMember("mentionedMember");

        return queryFactory
                .selectFrom(comment1)
                .leftJoin(comment1.member, member).fetchJoin() // 작성자 정보 조인
                .leftJoin(comment1.mentionedMember, mentionedMember).fetchJoin() // 언급된 사용자 정보 조인
                .where(
                        comment1.parent.id.isNull(), // 부모 댓글만 조회
                        isTypeAndIdEqualTo(type, contentId) // 동적 조건
                )
                .orderBy(comment1.createDate.desc(), comment1.comment.asc())
                .fetch();
    }

    /**
     * 댓글 목록 조회 (페이징 + 최신순)
     */
    public Page<CommentSaveResponse> getCommentList(CommentType type, Long contentId, Pageable pageable,
                                                    UUID loginUser) {
        QMember mentionedMember = new QMember("mentionedMember");

        Expression<Boolean> isAdmin = new CaseBuilder()
                .when(comment1.member.role.eq(MemberRole.ADMIN))
                .then(true)
                .otherwise(false);

        List<CommentSaveResponse> comments = queryFactory
                .select(Projections.fields(CommentSaveResponse.class,
                        ExpressionUtils.as(comment1.id, "commentId"),
                        comment1.createdIp,
                        comment1.member.publicId,
                        comment1.member.nickname,
                        ExpressionUtils.as(isAdmin, "isAdmin"),
                        comment1.member.imgUrl.as("commenterImg"),
                        comment1.imageUrl,
                        comment1.comment,
                        ExpressionUtils.as(Expressions.constant(false), "isRecommended"), // 기본값 false
                        comment1.recommendCount,
                        ExpressionUtils.as(comment1.mentionedMember.publicId, "mentionedPublicId"),
                        ExpressionUtils.as(comment1.mentionedMember.nickname, "mentionedNickname"),
                        comment1.createDate,
                        comment1.lastModifiedDate
                ))
                .from(comment1)
                .leftJoin(comment1.member, member) // 작성자 정보 조인
                .leftJoin(comment1.mentionedMember, mentionedMember) // 언급된 사용자 정보 조인
                .where(
                        comment1.parent.id.isNull(),
                        isTypeAndIdEqualTo(type, contentId)
                )
                .orderBy(comment1.createDate.desc(), comment1.comment.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        for (CommentSaveResponse response : comments) {
            response.setCreatedIp(ClientUtils.maskIp(response.getCreatedIp()));
            getCommentReply(response, loginUser);
            if (loginUser != null) {
                boolean isRecommend = commentRecommendReadService.isRecommended(response.getCommentId(), loginUser);
                response.setRecommended(isRecommend);
            }
        }

        long parentsElements = getTotalCommentCount(type, contentId); // 부모 댓글만 count

        // 부모 댓글만 count + 대댓글만 count
        long totalElements = parentsElements + getTotalReplyCommentCount(type, contentId);

        // totalPages는 부모 댓글로만 count
        long totalPages = (long) Math.ceil((double) parentsElements / pageable.getPageSize());

        // 응답 content는 댓글+대댓글 모두 포함, 페이징은 부모 댓글로만 페이징 하도록 설정 (total Elements 값은 부모 댓글 + 대댓글 모두 포함)
        return new CustomPageImpl<>(comments, pageable, totalElements, totalPages);
    }

    /**
     * 대댓글만 카운트
     */
    private long getTotalReplyCommentCount(CommentType type, Long contentId) {
        return ofNullable(
                queryFactory
                        .select(comment1.count())
                        .from(comment1)
                        .where(
                                comment1.parent.id.isNotNull(),
                                isTypeAndIdEqualTo(type, contentId)
                        )
                        .fetchOne()
        ).orElse(0L);
    }

    /**
     * 부모 댓글만 카운트
     */
    public long getTotalCommentCount(CommentType type, Long contentId) {
        return ofNullable(
                queryFactory
                        .select(comment1.count())
                        .from(comment1)
                        .where(
                                comment1.parent.id.isNull(),
                                isTypeAndIdEqualTo(type, contentId)
                        )
                        .fetchOne()
        ).orElse(0L);
    }

    public void getCommentReply(CommentSaveResponse parentComment, UUID loginUser) {
        QMember mentionedMember = new QMember("mentionedMember");

        Expression<Boolean> isAdmin = new CaseBuilder()
                .when(comment1.member.role.eq(MemberRole.ADMIN))
                .then(true)
                .otherwise(false);

        List<CommentSaveResponse> replies = queryFactory
                .select(Projections.fields(CommentSaveResponse.class,
                        ExpressionUtils.as(comment1.id, "commentId"),
                        comment1.createdIp,
                        comment1.member.publicId,
                        comment1.member.nickname,
                        ExpressionUtils.as(isAdmin, "isAdmin"),
                        comment1.member.imgUrl.as("commenterImg"),
                        comment1.imageUrl,
                        comment1.comment,
                        ExpressionUtils.as(Expressions.constant(false), "isRecommended"), // 기본값 false
                        comment1.recommendCount,
                        ExpressionUtils.as(comment1.mentionedMember.publicId, "mentionedPublicId"),
                        ExpressionUtils.as(comment1.mentionedMember.nickname, "mentionedNickname"),
                        comment1.createDate,
                        comment1.lastModifiedDate
                ))
                .from(comment1)
                .leftJoin(comment1.member, member)
                .leftJoin(comment1.mentionedMember, mentionedMember)
                .where(
                        comment1.parent.id.eq(parentComment.getCommentId()) // 부모 댓글 기준 대댓글 조회
                )
                .orderBy(comment1.createDate.asc()) // 대댓글은 오래된 순으로 정렬
                .fetch();

        for (CommentSaveResponse response : replies) {
            response.setCreatedIp(ClientUtils.maskIp(response.getCreatedIp()));
            if (loginUser != null) {
                boolean isRecommend = commentRecommendReadService.isRecommended(response.getCommentId(), loginUser);
                response.setRecommended(isRecommend);
            }
            // DFS를 사용하여 자식 댓글 탐색
            getCommentReply(response, loginUser);
        }

        parentComment.setReplyList(replies); // 대댓글을 부모 댓글에 매핑
    }

    /**
     * 베스트 댓글 목록 조회 (추천 수 기준 정렬)
     */
    public Page<BestCommentResponse> getBestCommentList(CommentType type, Long contentId, Pageable pageable) {
        QMember mentionedMember = new QMember("mentionedMember");
        QComment parentComment = new QComment("parentComment");
        QMember parentMember = new QMember("parentMember");

        Expression<Boolean> isAdmin = new CaseBuilder()
                .when(comment1.member.role.eq(MemberRole.ADMIN))
                .then(true)
                .otherwise(false);

        // ✅ 베스트 댓글 조회 (추천순 정렬)
        List<BestCommentResponse> bestComments = queryFactory
                .select(Projections.constructor(BestCommentResponse.class,
                        ExpressionUtils.as(comment1.id, "commentId"),
                        comment1.createdIp,
                        ExpressionUtils.as(comment1.member.publicId, "publicId"),
                        ExpressionUtils.as(comment1.member.nickname, "nickname"),
                        ExpressionUtils.as(isAdmin, "isAdmin"),
                        comment1.member.imgUrl.as("commenterImg"),
                        comment1.imageUrl.as("imageUrl"),
                        comment1.comment,
                        ExpressionUtils.as(Expressions.constant(false), "isRecommended"), // 기본값 false
                        comment1.recommendCount,
                        ExpressionUtils.as(comment1.mentionedMember.publicId, "mentionedPublicId"),
                        ExpressionUtils.as(comment1.mentionedMember.nickname, "mentionedNickname"),
                        comment1.createDate,
                        comment1.lastModifiedDate,
                        comment1.parent.member.nickname,
                        comment1.parent.member.publicId
                ))
                .from(comment1)
                .leftJoin(comment1.member, member) // 작성자 정보 조인
                .leftJoin(comment1.mentionedMember, mentionedMember) // 언급된 사용자 정보 조인
                .leftJoin(comment1.parent, parentComment)
                .leftJoin(parentComment.member, parentMember)
                .where(
                        isTypeAndIdEqualTo(type, contentId)
                )
                .orderBy(
                        comment1.recommendCount.desc(),
                        comment1.comment.asc(),
                        comment1.createDate.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = getTotalBestComment(type, contentId);

        return new PageImpl<>(bestComments, pageable, total);
    }

    private long getTotalBestComment(CommentType type, Long contentId) {
        return ofNullable(
                queryFactory
                        .select(comment1.count())
                        .from(comment1)
                        .where(isTypeAndIdEqualTo(type, contentId))
                        .fetchOne()
        ).orElse(0L);
    }

    private BooleanExpression isTypeAndIdEqualTo(CommentType type, Long contentId) {
        if (type == null || contentId == null) {
            return null;
        }

        switch (type) {
            case BOARD -> {
                return comment1.as(QBoardComment.class).board.id.eq(contentId);
            }
            case IMPROVEMENT -> {
                return comment1.as(QImprovementComment.class).improvement.id.eq(contentId);
            }
            case INQUIRY -> {
                return comment1.as(QInquiryComment.class).inquiry.id.eq(contentId);
            }
            case NEWS -> {
                return comment1.as(QNewsComment.class).news.id.eq(contentId);
            }
            case NOTICE -> {
                return comment1.as(QNoticeComment.class).notice.id.eq(contentId);
            }
            case MATCH -> {
                return comment1.as(QMatchComment.class).match.id.eq(contentId);
            }
            default -> {
                return null;
            }
        }
    }

    public Page<MyCommentDto> getMyCommentList(UUID publicId, CommentType commentType, BoardOrderType orderType,
                                               BoardSearchType searchType, String search, Pageable pageable) {
        QMember member = new QMember("member");
        QMember mentionedMember = new QMember("mentionedMember");

        Expression<Boolean> isAdmin = new CaseBuilder()
                .when(comment1.member.role.eq(MemberRole.ADMIN))
                .then(true)
                .otherwise(false);

        // 댓글 정보 조회
        JPQLQuery<CommentSaveResponse> comments = queryFactory
                .select(Projections.fields(CommentSaveResponse.class,
                        ExpressionUtils.as(comment1.id, "commentId"),
                        comment1.createdIp,
                        comment1.member.publicId,
                        comment1.member.nickname,
                        ExpressionUtils.as(isAdmin, "isAdmin"),
                        comment1.member.imgUrl.as("commenterImg"),
                        comment1.imageUrl,
                        comment1.comment,
                        ExpressionUtils.as(Expressions.constant(false), "isRecommended"), // 기본값 false
                        comment1.recommendCount,
                        ExpressionUtils.as(comment1.mentionedMember.publicId, "mentionedPublicId"),
                        ExpressionUtils.as(comment1.mentionedMember.nickname, "mentionedNickname"),
                        comment1.createDate,
                        comment1.lastModifiedDate
                ))
                .from(comment1)
                .leftJoin(comment1.member, member) // 작성자 정보 조인
                .leftJoin(comment1.mentionedMember, mentionedMember) // 언급된 사용자 정보 조인
                .leftJoin(boardComment).on(comment1.id.eq(boardComment.id))
                .leftJoin(board).on(boardComment.board.id.eq(board.id))
                .leftJoin(newsComment).on(comment1.id.eq(newsComment.id))
                .leftJoin(news).on(newsComment.news.id.eq(news.id))
                .leftJoin(noticeComment).on(comment1.id.eq(noticeComment.id))
                .leftJoin(notice).on(noticeComment.notice.id.eq(notice.id))
                .leftJoin(improvementComment).on(comment1.id.eq(improvementComment.id))
                .leftJoin(improvement).on(improvementComment.improvement.id.eq(improvement.id))
                .leftJoin(inquiryComment).on(comment1.id.eq(inquiryComment.id))
                .leftJoin(inquiry).on(inquiryComment.inquiry.id.eq(inquiry.id))
                .where(
                        comment1.member.publicId.eq(publicId),
                        commentType != CommentType.ALL ? comment1.commentType.stringValue().eq(commentType.name())
                                : null,
                        isSearchTypeLikeTo(searchType, search, commentType)
                )
                .orderBy(isOrderTypeEqualTo(orderType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<MyCommentDto> commentList = new ArrayList<>();

        for (CommentSaveResponse comment : comments.fetch()) {
            // 댓글이 달린 게시글 정보 조회
            PostResponse postResponse = queryFactory
                    .select(Projections.constructor(PostResponse.class,
                            comment1.commentType,
                            selectPostId(),
                            selectThumbnail(),
                            selectTitle(),
                            selectBoardType(),
                            selectCategoryType(),
                            selectCreatedIp(),
                            selectPublicId(),
                            selectNickname(),
                            selectCommentCount(),
                            selectCreateDate(),
                            selectLastModifiedDate(),
                            isHotPost(commentType),
                            isNewPost(commentType)
                    ))
                    .from(comment1)
                    .where(comment1.id.eq(comment.getCommentId()))
                    .fetchOne();

            // MyCommentDto 생성 및 리스트 추가
            MyCommentDto commentDto = new MyCommentDto();
            commentDto.setPostResponse(postResponse);
            commentDto.setCommentResponse(comment);
            commentList.add(commentDto);
        }

        for (MyCommentDto dto : commentList) {
            dto.getCommentResponse().setCreatedIp(
                    ClientUtils.maskIp(dto.getCommentResponse().getCreatedIp()));

            dto.getPostResponse().setCreatedIp(
                    ClientUtils.maskIp(dto.getPostResponse().getCreatedIp()));
        }

        // 전체 댓글 수 조회
        long totalCount = queryFactory
                .select(comment1.count())
                .from(comment1)
                .leftJoin(boardComment).on(comment1.id.eq(boardComment.id))
                .leftJoin(board).on(boardComment.board.id.eq(board.id))
                .leftJoin(newsComment).on(comment1.id.eq(newsComment.id))
                .leftJoin(news).on(newsComment.news.id.eq(news.id))
                .leftJoin(noticeComment).on(comment1.id.eq(noticeComment.id))
                .leftJoin(notice).on(noticeComment.notice.id.eq(notice.id))
                .leftJoin(improvementComment).on(comment1.id.eq(improvementComment.id))
                .leftJoin(improvement).on(improvementComment.improvement.id.eq(improvement.id))
                .leftJoin(inquiryComment).on(comment1.id.eq(inquiryComment.id))
                .leftJoin(inquiry).on(inquiryComment.inquiry.id.eq(inquiry.id))
                .where(
                        comment1.member.publicId.eq(publicId),
                        commentType != CommentType.ALL ? comment1.commentType.stringValue().eq(commentType.name())
                                : null,
                        isSearchTypeLikeTo(searchType, search, commentType)
                )
                .fetchOne();

        return new PageImpl<>(commentList, pageable, totalCount);
    }

    private BooleanExpression isSearchTypeLikeTo(BoardSearchType searchType, String search, CommentType commentType) {
        if (searchType == null || search == null || search.isBlank()) {
            return null;
        }

        switch (searchType) {
            case TITLE:
                return switch (commentType) {
                    // MATCH는 title 검색이 없으므로 제외, INQUIRY는 content를 title처럼 검색하게 해뒀음
                    case ALL -> board.title.like("%" + search + "%")
                            .or(improvement.title.like("%" + search + "%"))
                            .or(news.title.like("%" + search + "%"))
                            .or(notice.title.like("%" + search + "%"))
                            .or(inquiry.content.like("%" + search + "%"));
                    case BOARD -> board.title.like("%" + search + "%");
                    case IMPROVEMENT -> improvement.title.like("%" + search + "%");
                    case NEWS -> news.title.like("%" + search + "%");
                    case NOTICE -> notice.title.like("%" + search + "%");
                    case INQUIRY -> inquiry.content.like("%" + search + "%");
                    case MATCH -> null;
                };

            case CONTENT:
                return switch (commentType) {
                    // MATCH는 content 없음
                    case ALL -> board.content.like("%" + search + "%")
                            .or(improvement.content.like("%" + search + "%"))
                            .or(news.content.like("%" + search + "%"))
                            .or(notice.content.like("%" + search + "%"))
                            .or(inquiry.content.like("%" + search + "%"));
                    case BOARD -> board.content.like("%" + search + "%");
                    case IMPROVEMENT -> improvement.content.like("%" + search + "%");
                    case NEWS -> news.content.like("%" + search + "%");
                    case NOTICE -> notice.content.like("%" + search + "%");
                    case INQUIRY -> inquiry.content.like("%" + search + "%");
                    case MATCH -> null;
                };

            case TITLE_CONTENT:
                return switch (commentType) {
                    case ALL -> board.title.like("%" + search + "%").or(board.content.like("%" + search + "%"))
                            .or(improvement.title.like("%" + search + "%")
                                    .or(improvement.content.like("%" + search + "%")))
                            .or(news.title.like("%" + search + "%").or(news.content.like("%" + search + "%")))
                            .or(notice.title.like("%" + search + "%").or(notice.content.like("%" + search + "%")))
                            .or(inquiry.content.like("%" + search + "%"));
                    case BOARD -> board.title.like("%" + search + "%").or(board.content.like("%" + search + "%"));
                    case IMPROVEMENT ->
                            improvement.title.like("%" + search + "%").or(improvement.content.like("%" + search + "%"));
                    case NEWS -> news.title.like("%" + search + "%").or(news.content.like("%" + search + "%"));
                    case NOTICE -> notice.title.like("%" + search + "%").or(notice.content.like("%" + search + "%"));
                    case INQUIRY -> inquiry.content.like("%" + search + "%");
                    case MATCH -> null;
                };

            case NICKNAME:
                return comment1.member.nickname.like("%" + search + "%");

            case COMMENT:
                return comment1.comment.like("%" + search + "%");

            default:
                return null;
        }
    }

    private BooleanExpression isHotPost(CommentType commentType) {
        // commentType이 BOARD일 경우에만 핫 게시글 체크
        if (commentType.equals(CommentType.BOARD)) {
            List<Long> hotBoardIds = getHotBoardIdList(); // 핫 게시글 ID 목록 가져오기

            if (hotBoardIds == null || hotBoardIds.isEmpty()) {
                return Expressions.FALSE; // 핫 게시글이 없으면 false 반환
            }

            // 게시글 ID가 핫 게시글 목록에 포함되는지 체크
            return JPAExpressions.select(board.id)
                    .from(board)
                    .join(boardComment).on(boardComment.board.id.eq(board.id))
                    .where(boardComment.id.eq(comment1.id))
                    .in(hotBoardIds);  // board.id가 hotBoardIds 목록에 포함되는지 체크
        }

        // commentType이 BOARD가 아니면 false로 처리
        return Expressions.FALSE;
    }

    private BooleanExpression isNewPost(CommentType commentType) {
        // commentType이 BOARD일 경우에만 뉴 게시글 체크
        if (commentType.equals(CommentType.BOARD)) {
            List<Long> newBoardIds = getNewBoardIdList(); // 핫 게시글 ID 목록 가져오기

            if (newBoardIds == null || newBoardIds.isEmpty()) {
                return Expressions.FALSE; // 뉴 게시글이 없으면 false 반환
            }

            // 게시글 ID가 뉴 게시글 목록에 포함되는지 체크
            return JPAExpressions.select(board.id)
                    .from(board)
                    .join(boardComment).on(boardComment.board.id.eq(board.id))
                    .where(boardComment.id.eq(comment1.id))
                    .in(newBoardIds);  // board.id가 newBoardIds 목록에 포함되는지 체크
        }

        // commentType이 BOARD가 아니면 false로 처리
        return Expressions.FALSE;
    }


    private OrderSpecifier<?>[] isOrderTypeEqualTo(BoardOrderType orderType) {
        // default 최신순
        BoardOrderType boardOrderType = ofNullable(orderType).orElse(BoardOrderType.CREATE);
        return switch (boardOrderType) {
            case CREATE -> new OrderSpecifier<?>[]{comment1.createDate.desc(), comment1.id.desc()};
            case RECOMMEND -> new OrderSpecifier<?>[]{comment1.recommendCount.desc(), comment1.id.desc()};
            case COMMENT -> new OrderSpecifier<?>[]{
                    selectCommentCountDesc(),
                    comment1.id.desc()            // 동일한 경우 id 기준 정렬
            };
        };
    }

    private OrderSpecifier<Integer> selectCommentCountDesc() {
        return new OrderSpecifier<>(Order.DESC, selectCommentCount());
    }

    /**
     * 핫 게시글 ID 목록 조회
     */
    private List<Long> getHotBoardIdList() {
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

    /**
     * 실시간 최신 게시글 ID 목록 조회
     */
    private List<Long> getNewBoardIdList() {
        // 전체 게시글 기준 생성일 내림차순으로 최신 10개 가져오기
        List<Long> boards = queryFactory
                .select(board.id)
                .from(board)
                .orderBy(board.createDate.desc(), board.id.desc())
                .limit(10)
                .fetch();

        return boards;
    }

    /**
     * 게시글의 썸네일을 가져오는 메서드
     *
     * @brief: BOARD
     * @brief: NOTICE
     * @brief: NEWS
     * @brief: IMPROVEMENT
     */
    private Expression<String> selectThumbnail() {
        return new CaseBuilder()
                .when(comment1.commentType.eq(CommentType.BOARD))
                .then(JPAExpressions.select(board.thumbnail)
                        .from(board)
                        .join(boardComment).on(boardComment.board.id.eq(board.id))
                        .where(boardComment.id.eq(comment1.id)))
                .when(comment1.commentType.eq(CommentType.NEWS))
                .then(JPAExpressions.select(news.thumbImg)
                        .from(news)
                        .join(newsComment)
                        .on(newsComment.news.id.eq(news.id))
                        .where(newsComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.NOTICE))
                .then(JPAExpressions.select(notice.imgUrl)
                        .from(notice)
                        .join(noticeComment)
                        .on(noticeComment.notice.id.eq(notice.id))
                        .where(noticeComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.IMPROVEMENT))
                .then(JPAExpressions.select(improvement.imgUrl)
                        .from(improvement)
                        .join(improvementComment)
                        .on(improvementComment.improvement.id.eq(improvement.id))
                        .where(improvementComment.id.eq(comment1.id))
                )
                .otherwise(Expressions.nullExpression());
    }

    /**
     * 게시글의 id를 가져오는 메서드
     *
     * @brief: BOARD
     * @brief: NOTICE
     * @brief: NEWS
     * @brief: IMPROVEMENT
     * @brief: INQUIRY
     */
    private Expression<Long> selectPostId() {
        return new CaseBuilder()
                .when(comment1.commentType.eq(CommentType.BOARD))
                .then(JPAExpressions.select(board.id)
                        .from(board)
                        .join(boardComment)
                        .on(boardComment.board.id.eq(board.id))
                        .where(boardComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.NEWS))
                .then(JPAExpressions.select(news.id)
                        .from(news)
                        .join(newsComment)
                        .on(newsComment.news.id.eq(news.id))
                        .where(newsComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.NOTICE))
                .then(JPAExpressions.select(notice.id)
                        .from(notice)
                        .join(noticeComment)
                        .on(noticeComment.notice.id.eq(notice.id))
                        .where(noticeComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.IMPROVEMENT))
                .then(JPAExpressions.select(improvement.id)
                        .from(improvement)
                        .join(improvementComment)
                        .on(improvementComment.improvement.id.eq(improvement.id))
                        .where(improvementComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.INQUIRY))
                .then(JPAExpressions.select(inquiry.id)
                        .from(inquiry)
                        .join(inquiryComment)
                        .on(inquiryComment.inquiry.id.eq(inquiry.id))
                        .where(inquiryComment.id.eq(comment1.id))
                )
                .otherwise(Expressions.nullExpression());
    }

    /**
     * 게시글의 제목을 가져오는 메서드
     *
     * @brief: BOARD
     * @brief: NOTICE
     * @brief: NEWS
     * @brief: IMPROVEMENT
     * @brief: INQUIRY
     */
    private Expression<String> selectTitle() {
        return new CaseBuilder()
                .when(comment1.commentType.eq(CommentType.BOARD))
                .then(JPAExpressions.select(board.title)
                        .from(board)
                        .join(boardComment).on(boardComment.board.id.eq(board.id))
                        .where(boardComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.NEWS))
                .then(JPAExpressions.select(news.title)
                        .from(news)
                        .join(newsComment).on(newsComment.news.id.eq(news.id))
                        .where(newsComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.NOTICE))
                .then(JPAExpressions.select(notice.title)
                        .from(notice)
                        .join(noticeComment)
                        .on(noticeComment.notice.id.eq(notice.id))
                        .where(noticeComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.IMPROVEMENT))
                .then(JPAExpressions.select(improvement.title)
                        .from(improvement)
                        .join(improvementComment)
                        .on(improvementComment.improvement.id.eq(improvement.id))
                        .where(improvementComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.INQUIRY))
                .then(JPAExpressions.select(inquiry.content)
                        .from(inquiry)
                        .join(inquiryComment)
                        .on(inquiryComment.inquiry.id.eq(inquiry.id))
                        .where(inquiryComment.id.eq(comment1.id))
                )
                .otherwise(Expressions.nullExpression());
    }

    /**
     * 게시글의 게시판 타입을 가져오는 메서드
     *
     * @brief: BOARD
     * @brief: NEWS
     */
    private Expression<Category> selectBoardType() {
        return new CaseBuilder()
                .when(comment1.commentType.eq(CommentType.BOARD))
                .then(JPAExpressions.select(board.boardType)
                        .from(board)
                        .join(boardComment).on(boardComment.board.id.eq(board.id))
                        .where(boardComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.NEWS))
                .then(JPAExpressions.select(news.category)
                        .from(news)
                        .join(newsComment).on(newsComment.news.id.eq(news.id))
                        .where(newsComment.id.eq(comment1.id))
                )
                .otherwise(Expressions.constant(Category.UNKNOWN));
    }

    /**
     * 게시글의 카테고리 타입을 가져오는 메서드
     *
     * @brief: BOARD
     */
    private Expression<CategoryType> selectCategoryType() {
        return new CaseBuilder()
                .when(comment1.commentType.eq(CommentType.BOARD))
                .then(JPAExpressions.select(board.categoryType)
                        .from(board)
                        .join(boardComment).on(boardComment.board.id.eq(board.id))
                        .where(boardComment.id.eq(comment1.id))
                )
                .otherwise(Expressions.constant(CategoryType.UNKNOWN));
    }

    /**
     * 게시글의 댓글수를 가져오는 메서드
     *
     * @brief: BOARD
     * @brief: NOTICE
     * @brief: NEWS
     * @brief: IMPROVEMENT
     * @brief: INQUIRY
     */
    private Expression<Integer> selectCommentCount() {
        return new CaseBuilder()
                .when(comment1.commentType.eq(CommentType.BOARD))
                .then(JPAExpressions.select(board.boardCount.commentCount)
                        .from(board)
                        .join(boardComment).on(boardComment.board.id.eq(board.id))
                        .where(boardComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.NEWS))
                .then(JPAExpressions.select(newsCount.commentCount)
                        .from(news)
                        .join(newsCount)
                        .on(newsCount.news.id.eq(news.id))
                        .join(newsComment)
                        .on(newsComment.news.id.eq(news.id))
                        .where(newsComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.NOTICE))
                .then(JPAExpressions.select(notice.noticeCount.commentCount)
                        .from(notice)
                        .join(noticeComment)
                        .on(noticeComment.notice.id.eq(notice.id))
                        .where(noticeComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.IMPROVEMENT))
                .then(JPAExpressions.select(improvement.improvementCount.commentCount)
                        .from(improvement)
                        .join(improvementComment)
                        .on(improvementComment.improvement.id.eq(improvement.id))
                        .where(improvementComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.INQUIRY))
                .then(JPAExpressions.select(inquiry.inquiryCount.commentCount)
                        .from(inquiry)
                        .join(inquiryComment)
                        .on(inquiryComment.inquiry.id.eq(inquiry.id))
                        .where(inquiryComment.id.eq(comment1.id))
                )
                .otherwise(Expressions.constant(0));
    }

    /**
     * 게시글 작성자의 createdIp를 가져오는 메서드
     *
     * @brief: BOARD
     * @brief: NOTICE
     * @brief: IMPROVEMENT
     * @brief: INQUIRY
     */
    private Expression<String> selectCreatedIp() {
        return new CaseBuilder()
                .when(comment1.commentType.eq(CommentType.BOARD))
                .then(JPAExpressions.select(board.createdIp)
                        .from(board)
                        .where(board.id.eq(comment1.as(QBoardComment.class).board.id))
                )
                .when(comment1.commentType.eq(CommentType.NOTICE))
                .then(JPAExpressions.select(notice.createdIp)
                        .from(notice)
                        .join(noticeComment)
                        .on(noticeComment.notice.id.eq(notice.id))
                        .where(noticeComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.IMPROVEMENT))
                .then(JPAExpressions.select(improvement.createdIp)
                        .from(improvement)
                        .join(improvementComment)
                        .on(improvementComment.improvement.id.eq(improvement.id))
                        .where(improvementComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.INQUIRY))
                .then(JPAExpressions.select(inquiry.clientIp)
                        .from(inquiry)
                        .join(inquiryComment)
                        .on(inquiryComment.inquiry.id.eq(inquiry.id))
                        .where(inquiryComment.id.eq(comment1.id))
                )
                .otherwise(Expressions.nullExpression());
    }

    /**
     * 게시글 작성자의 publicId를 가져오는 메서드
     *
     * @brief: BOARD
     * @brief: NOTICE
     * @brief: IMPROVEMENT
     * @brief: INQUIRY
     */
    private Expression<UUID> selectPublicId() {
        return new CaseBuilder()
                .when(comment1.commentType.eq(CommentType.BOARD))
                .then(JPAExpressions.select(board.member.publicId)
                        .from(board)
                        .where(board.id.eq(comment1.as(QBoardComment.class).board.id))
                )
                .when(comment1.commentType.eq(CommentType.NOTICE))
                .then(JPAExpressions.select(notice.member.publicId)
                        .from(notice)
                        .join(noticeComment)
                        .on(noticeComment.notice.id.eq(notice.id))
                        .where(noticeComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.IMPROVEMENT))
                .then(JPAExpressions.select(improvement.member.publicId)
                        .from(improvement)
                        .join(improvementComment)
                        .on(improvementComment.improvement.id.eq(improvement.id))
                        .where(improvementComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.INQUIRY))
                .then(JPAExpressions.select(inquiry.member.publicId)
                        .from(inquiry)
                        .join(inquiryComment)
                        .on(inquiryComment.inquiry.id.eq(inquiry.id))
                        .where(inquiryComment.id.eq(comment1.id))
                )
                .otherwise(Expressions.nullExpression());
    }

    /**
     * 게시글 작성자의 nickname을 가져오는 메서드
     *
     * @brief: BOARD
     * @brief: NOTICE
     * @brief: IMPROVEMENT
     * @brief: INQUIRY
     */
    private Expression<String> selectNickname() {
        return new CaseBuilder()
                .when(comment1.commentType.eq(CommentType.BOARD))
                .then(JPAExpressions.select(board.member.nickname)
                        .from(board)
                        .where(board.id.eq(comment1.as(QBoardComment.class).board.id))
                )
                .when(comment1.commentType.eq(CommentType.NOTICE))
                .then(JPAExpressions.select(notice.member.nickname)
                        .from(notice)
                        .join(noticeComment)
                        .on(noticeComment.notice.id.eq(notice.id))
                        .where(noticeComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.IMPROVEMENT))
                .then(JPAExpressions.select(improvement.member.nickname)
                        .from(improvement)
                        .join(improvementComment)
                        .on(improvementComment.improvement.id.eq(improvement.id))
                        .where(improvementComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.INQUIRY))
                .then(JPAExpressions.select(inquiry.member.nickname)
                        .from(inquiry)
                        .join(inquiryComment)
                        .on(inquiryComment.inquiry.id.eq(inquiry.id))
                        .where(inquiryComment.id.eq(comment1.id))
                )
                .otherwise(Expressions.nullExpression());
    }

    /**
     * 게시글의 createDate를 가져오는 메서드
     *
     * @brief: BOARD
     * @brief: NOTICE
     * @brief: NEWS
     * @brief: IMPROVEMENT
     * @brief: INQUIRY
     */
    private Expression<LocalDateTime> selectCreateDate() {
        return new CaseBuilder()
                .when(comment1.commentType.eq(CommentType.BOARD))
                .then(JPAExpressions.select(board.createDate)
                        .from(board)
                        .where(board.id.eq(comment1.as(QBoardComment.class).board.id))
                )
                .when(comment1.commentType.eq(CommentType.NEWS))
                .then(JPAExpressions.select(news.createDate)
                        .from(news)
                        .where(news.id.eq(comment1.as(QNewsComment.class).news.id))
                )
                .when(comment1.commentType.eq(CommentType.NOTICE))
                .then(JPAExpressions.select(notice.member.createDate)
                        .from(notice)
                        .join(noticeComment)
                        .on(noticeComment.notice.id.eq(notice.id))
                        .where(noticeComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.IMPROVEMENT))
                .then(JPAExpressions.select(improvement.member.createDate)
                        .from(improvement)
                        .join(improvementComment)
                        .on(improvementComment.improvement.id.eq(improvement.id))
                        .where(improvementComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.INQUIRY))
                .then(JPAExpressions.select(inquiry.member.createDate)
                        .from(inquiry)
                        .join(inquiryComment)
                        .on(inquiryComment.inquiry.id.eq(inquiry.id))
                        .where(inquiryComment.id.eq(comment1.id))
                )
                .otherwise(Expressions.nullExpression());
    }

    /**
     * 게시글의 lastModifiedDate를 가져오는 메서드
     *
     * @brief: BOARD
     * @brief: NOTICE
     * @brief: NEWS
     * @brief: IMPROVEMENT
     * @brief: INQUIRY
     */
    private Expression<LocalDateTime> selectLastModifiedDate() {
        return new CaseBuilder()
                .when(comment1.commentType.eq(CommentType.BOARD))
                .then(JPAExpressions.select(board.lastModifiedDate)
                        .from(board)
                        .where(board.id.eq(comment1.as(QBoardComment.class).board.id))
                )
                .when(comment1.commentType.eq(CommentType.NEWS))
                .then(JPAExpressions.select(news.lastModifiedDate)
                        .from(news)
                        .where(news.id.eq(comment1.as(QNewsComment.class).news.id))
                )
                .when(comment1.commentType.eq(CommentType.NOTICE))
                .then(JPAExpressions.select(notice.member.lastModifiedDate)
                        .from(notice)
                        .join(noticeComment)
                        .on(noticeComment.notice.id.eq(notice.id))
                        .where(noticeComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.IMPROVEMENT))
                .then(JPAExpressions.select(improvement.member.lastModifiedDate)
                        .from(improvement)
                        .join(improvementComment)
                        .on(improvementComment.improvement.id.eq(improvement.id))
                        .where(improvementComment.id.eq(comment1.id))
                )
                .when(comment1.commentType.eq(CommentType.INQUIRY))
                .then(JPAExpressions.select(inquiry.member.lastModifiedDate)
                        .from(inquiry)
                        .join(inquiryComment)
                        .on(inquiryComment.inquiry.id.eq(inquiry.id))
                        .where(inquiryComment.id.eq(comment1.id))
                )
                .otherwise(Expressions.nullExpression());
    }
}