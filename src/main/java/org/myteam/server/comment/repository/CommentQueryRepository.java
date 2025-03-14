package org.myteam.server.comment.repository;

import static org.myteam.server.comment.domain.QComment.comment1;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.comment.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final CommentRepository commentRepository;

    public List<Comment> findRepliesWithImages(Long parentId) {
        return queryFactory
                .selectFrom(comment1)
                .where(comment1.parent.id.eq(parentId)
                        .and(comment1.imageUrl.isNotNull()))
                .fetch();
    }

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

//    public Page<NoticeComment> findByNoticeId(Long noticeId, Pageable pageable) {
//        QNoticeComment noticeComment = QNoticeComment.noticeComment;
//        QMember member = QMember.member;
//        QComment parent = new QComment("parent"); // 부모 댓글 alias
//
//        // 전체 개수 조회
//        long total = queryFactory
//                .select(noticeComment.count())
//                .from(noticeComment)
//                .where(noticeComment.notice.id.eq(noticeId))
//                .fetchOne();
//
//        // 댓글 목록 조회 (N+1 문제 방지: `member`, `parent`를 JOIN FETCH)
//        List<NoticeComment> results = queryFactory
//                .selectFrom(noticeComment)
//                .leftJoin(noticeComment.member, member).fetchJoin()
//                .leftJoin(noticeComment.parent, parent).fetchJoin()
//                .where(noticeComment.notice.id.eq(noticeId))
//                .orderBy(noticeComment.createDate.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        return new PageImpl<>(results, pageable, total);
//    }
//
//    public Page<NoticeComment> findMyComments(UUID memberId, Pageable pageable) {
//        QNoticeComment noticeComment = QNoticeComment.noticeComment;
//        QMember member = QMember.member;
//        QComment parent = new QComment("parent"); // 부모 댓글 alias
//
//        // 전체 개수 조회
//        long total = queryFactory
//                .select(noticeComment.count())
//                .from(noticeComment)
//                .where(noticeComment.member.publicId.eq(memberId))
//                .fetchOne();
//
//        // 내가 작성한 댓글 목록 조회
//        List<NoticeComment> results = queryFactory
//                .select(noticeComment)
//                .from(noticeComment)
//                .leftJoin(noticeComment.member, member).fetchJoin()
//                .leftJoin(noticeComment.parent, parent).fetchJoin()
//                .where(noticeComment.member.publicId.eq(memberId))
//                .orderBy(noticeComment.createDate.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        return new PageImpl<>(results, pageable, total);
//    }
}
