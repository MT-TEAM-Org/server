package org.myteam.server.board.repository;

import java.util.List;
import org.myteam.server.board.domain.BoardReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardReplyRepository extends JpaRepository<BoardReply, Long> {
    List<BoardReply> findByBoardCommentId(Long boardCommentId);
}