package org.myteam.server.comment.repository;

import org.myteam.server.comment.domain.CommentRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRecommendRepository extends JpaRepository<CommentRecommend, Long> {
    Optional<CommentRecommend> findByCommentIdAndMemberPublicId(Long commentId, UUID publicId);
    void deleteByCommentIdAndMemberPublicId(Long commentId, UUID publicId);
    void deleteByCommentId(Long boardCommentId);
}
