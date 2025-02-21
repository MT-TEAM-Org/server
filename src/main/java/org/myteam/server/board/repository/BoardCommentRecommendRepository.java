package org.myteam.server.board.repository;

import java.util.Optional;
import java.util.UUID;
import org.myteam.server.board.domain.BoardCommentRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCommentRecommendRepository extends JpaRepository<BoardCommentRecommend, Long> {
    Optional<BoardCommentRecommend> findByBoardCommentIdAndMemberPublicId(Long boardCommentId, UUID publicId);

    void deleteByBoardCommentIdAndMemberPublicId(Long boardCommentId, UUID publicId);

    void deleteByBoardCommentId(Long boardCommentId);
}