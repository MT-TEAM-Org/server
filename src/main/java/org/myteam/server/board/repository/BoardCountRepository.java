package org.myteam.server.board.repository;

import java.util.Optional;
import org.myteam.server.board.domain.BoardCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardCountRepository extends JpaRepository<BoardCount, Long> {

    void deleteByBoardId(Long boardId);

    Optional<BoardCount> findByBoardId(Long boardId);

    @Query("SELECT b.viewCount FROM BoardCount b WHERE b.id = :boardId")
    int findViewCountById(@Param("boardId") Long boardId);

    @Modifying
    @Query("UPDATE BoardCount b SET b.viewCount = :viewCount WHERE b.id = :boardId")
    void updateViewCount(@Param("boardId") Long boardId, @Param("viewCount") int viewCount);

    @Modifying
    @Query("UPDATE BoardCount b SET b.viewCount = :view, b.commentCount = :comment, b.recommendCount = :recommend WHERE b.id = :boardId")
    void updateAllCounts(@Param("boardId") Long boardId,
                         @Param("view") int view,
                         @Param("comment") int comment,
                         @Param("recommend") int recommend);

}