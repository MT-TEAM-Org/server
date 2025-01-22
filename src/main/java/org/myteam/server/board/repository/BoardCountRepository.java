package org.myteam.server.board.repository;

import org.myteam.server.board.entity.BoardCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCountRepository extends JpaRepository<BoardCount, Long> {
    void deleteByBoardId(Long id);
}