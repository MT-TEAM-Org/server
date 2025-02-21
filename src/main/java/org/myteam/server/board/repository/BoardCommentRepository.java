package org.myteam.server.board.repository;

import java.util.List;
import org.myteam.server.board.domain.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    List<BoardComment> findAllByBoardId(Long boardId);
}