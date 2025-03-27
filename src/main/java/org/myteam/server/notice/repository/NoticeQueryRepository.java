package org.myteam.server.notice.repository;

import static java.util.Optional.ofNullable;
import static org.myteam.server.comment.domain.QNoticeComment.noticeComment;
import static org.myteam.server.member.entity.QMember.member;
import static org.myteam.server.notice.domain.QNotice.notice;
import static org.myteam.server.notice.domain.QNoticeCount.noticeCount;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.dto.reponse.CommentSearchDto;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.domain.QComment;
import org.myteam.server.comment.domain.QNoticeComment;
import org.myteam.server.notice.domain.NoticeSearchType;
import org.myteam.server.notice.dto.response.NoticeResponse.NoticeDto;
import org.myteam.server.util.ClientUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NoticeQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 공지사항 목록 조회
     */
    public Page<NoticeDto> getNoticeList(NoticeSearchType searchType, String search, Pageable pageable) {

        List<NoticeDto> content = queryFactory
                .select(Projections.fields(NoticeDto.class,
                        notice.id,
                        notice.title,
                        notice.imgUrl,
                        notice.createdIp.as("createdIp"),
                        notice.member.publicId,
                        notice.member.nickname,
                        noticeCount.commentCount,
                        noticeCount.recommendCount,
                        notice.createDate.as("createdAt"),
                        notice.lastModifiedDate.as("updatedAt")
                ))
                .from(notice)
                .join(noticeCount).on(noticeCount.notice.id.eq(notice.id))
                .join(member).on(member.eq(notice.member))
                .fetchJoin()
                .where(isSearchTypeLikeTo(searchType, search))
                .orderBy(notice.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = getTotalNoticeCount(searchType, search);

        for (NoticeDto noticeDto : content) {
            noticeDto.setCreatedIp(ClientUtils.maskIp(noticeDto.getCreatedIp()));
        }

        if (searchType == NoticeSearchType.COMMENT) {
            content.forEach(noticeDto -> {
                CommentSearchDto commentSearch = getSearchNoticeComment(noticeDto.getId(), search);
                noticeDto.setCommentSearchList(commentSearch);
            });
        }

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression isSearchTypeLikeTo(NoticeSearchType noticeSearchType, String search) {
        if (search == null || search.isEmpty()) {
            return null;
        }

        return switch (noticeSearchType) {
            case TITLE -> notice.title.like("%" + search + "%");
            case CONTENT -> notice.content.like("%" + search + "%");
            case TITLE_CONTENT -> notice.title.like("%" + search + "%")
                    .or(notice.content.like("%" + search + "%"));
            case NICKNAME -> notice.member.nickname.like("%" + search + "%");
            case COMMENT -> JPAExpressions.selectOne()
                    .from(QComment.comment1)
                    .where(
                            QComment.comment1.comment.like("%" + search + "%")
                                    .and(QComment.comment1.commentType.eq(CommentType.NOTICE))
                                    .and(QComment.comment1.as(QNoticeComment.class).notice.id.eq(
                                            notice.id))
                    )
                    .exists();
            default -> null;
        };
    }

    private long getTotalNoticeCount(NoticeSearchType searchType, String search) {
        return ofNullable(
                queryFactory
                        .select(notice.count())
                        .from(notice)
                        .where(isSearchTypeLikeTo(searchType, search))
                        .fetchOne()
        ).orElse(0L);
    }

    private CommentSearchDto getSearchNoticeComment(Long noticeId, String search) {
        JPQLQuery<CommentSearchDto> query = queryFactory
                .select(Projections.fields(CommentSearchDto.class,
                        noticeComment.id.as("commentId"),
                        noticeComment.comment,
                        noticeComment.imageUrl
                ))
                .from(noticeComment)
                .where(
                        noticeComment.notice.id.eq(noticeId),
                        noticeComment.comment.like("%" + search + "%")
                );

        return query.orderBy(
                noticeComment.createDate.desc(),
                noticeComment.comment.asc()
        ).fetchFirst();
    }

    public List<NoticeDto> getFixNotice() {
        return queryFactory
                .select(Projections.fields(NoticeDto.class,
                        notice.id,
                        notice.title,
                        notice.imgUrl.as("thumbnail"),
                        notice.createdIp.as("createdIp"),
                        notice.member.publicId,
                        notice.member.nickname,
                        noticeCount.commentCount,
                        noticeCount.recommendCount,
                        notice.createDate.as("createdAt"),
                        notice.lastModifiedDate.as("updatedAt")
                ))
                .from(notice)
                .join(noticeCount).on(noticeCount.notice.id.eq(notice.id))
                .join(member).on(member.eq(notice.member))
                .fetchJoin()
                .orderBy(notice.createDate.desc())
                .limit(2)
                .fetch();
    }

    public Long findPreviousNoticeId(Long noticeId) {
        return queryFactory
                .select(notice.id)
                .from(notice)
                .where(notice.id.lt(noticeId)) // 현재 게시글보다 작은 ID
                .orderBy(notice.id.desc()) // 가장 큰 ID (즉, 이전 글)
                .limit(1)
                .fetchOne();
    }

    public Long findNextNoticeId(Long noticeId) {
        return queryFactory
                .select(notice.id)
                .from(notice)
                .where(notice.id.gt(noticeId)) // 현재 게시글보다 큰 ID
                .orderBy(notice.id.asc()) // 가장 작은 ID (즉, 다음 글)
                .limit(1)
                .fetchOne();
    }
}
