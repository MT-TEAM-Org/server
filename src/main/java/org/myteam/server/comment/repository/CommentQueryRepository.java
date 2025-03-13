package org.myteam.server.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.domain.NoticeComment;
import org.myteam.server.comment.domain.QComment;
import org.myteam.server.comment.domain.QNoticeComment;
import org.myteam.server.member.entity.QMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<NoticeComment> findByNoticeId(Long noticeId, Pageable pageable) {
        QNoticeComment noticeComment = QNoticeComment.noticeComment;
        QMember member = QMember.member;
        QComment parent = new QComment("parent"); // 부모 댓글 alias

        // 전체 개수 조회
        long total = queryFactory
                .select(noticeComment.count())
                .from(noticeComment)
                .where(noticeComment.notice.id.eq(noticeId))
                .fetchOne();

        // 댓글 목록 조회 (N+1 문제 방지: `member`, `parent`를 JOIN FETCH)
        List<NoticeComment> results = queryFactory
                .selectFrom(noticeComment)
                .leftJoin(noticeComment.member, member).fetchJoin()
                .leftJoin(noticeComment.parent, parent).fetchJoin()
                .where(noticeComment.notice.id.eq(noticeId))
                .orderBy(noticeComment.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public Page<NoticeComment> findMyComments(UUID memberId, Pageable pageable) {
        QNoticeComment noticeComment = QNoticeComment.noticeComment;
        QMember member = QMember.member;
        QComment parent = new QComment("parent"); // 부모 댓글 alias

        // 전체 개수 조회
        long total = queryFactory
                .select(noticeComment.count())
                .from(noticeComment)
                .where(noticeComment.member.publicId.eq(memberId))
                .fetchOne();

        // 내가 작성한 댓글 목록 조회
        List<NoticeComment> results = queryFactory
                .select(noticeComment)
                .from(noticeComment)
                .leftJoin(noticeComment.member, member).fetchJoin()
                .leftJoin(noticeComment.parent, parent).fetchJoin()
                .where(noticeComment.member.publicId.eq(memberId))
                .orderBy(noticeComment.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }
}
