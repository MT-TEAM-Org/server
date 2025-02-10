package org.myteam.server.board.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.myteam.server.board.domain.BoardCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCountRepository extends JpaRepository<BoardCount, Long> {

    void deleteByBoardId(Long id);

    // 비관적 락으로 동시성 문제 해결
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BoardCount> findByBoardId(Long boardId);
}