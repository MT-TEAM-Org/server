package org.myteam.server.board.repository;

import java.util.Optional;
import org.myteam.server.board.domain.BoardCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCountRepository extends JpaRepository<BoardCount, Long> {

    void deleteByBoardId(Long boardId);

    Optional<BoardCount> findByBoardId(Long boardId);
}