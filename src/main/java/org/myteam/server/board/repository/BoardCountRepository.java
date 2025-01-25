package org.myteam.server.board.repository;

import org.myteam.server.board.entity.BoardCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCountRepository extends JpaRepository<BoardCount, Long> {
    void deleteByBoardId(Long id);
}