package org.myteam.server.board.repository;

import java.util.Optional;
import java.util.UUID;
import org.myteam.server.board.domain.BoardRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRecommendRepository extends JpaRepository<BoardRecommend, Long> {
    
    Optional<BoardRecommend> findByBoardIdAndMemberPublicId(Long boardId, UUID memberId);

    void deleteByBoardIdAndMemberPublicId(Long boardId, UUID publicId);
}