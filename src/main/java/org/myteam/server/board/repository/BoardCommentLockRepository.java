package org.myteam.server.board.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.myteam.server.board.domain.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCommentLockRepository extends JpaRepository<BoardComment, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BoardComment> findById(Long boardCommentId);
}