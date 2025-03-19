package org.myteam.server.comment.repository;

import java.util.Optional;
import java.util.UUID;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.domain.NoticeComment;
import org.myteam.server.notice.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<NoticeComment> findByNoticeOrderByCreateDateDesc(Notice notice, Pageable pageable);

    Page<Comment> findByMemberPublicId(UUID memberPublicId, Pageable pageable);

    Optional<Comment> findCommentById(Long commentId);

}
