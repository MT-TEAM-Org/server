package org.myteam.server.board.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.myteam.server.board.domain.BoardCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCountLockRepository extends JpaRepository<BoardCount, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BoardCount> findByBoardId(long boardId);
}