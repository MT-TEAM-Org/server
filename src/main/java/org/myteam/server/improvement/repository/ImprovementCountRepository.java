package org.myteam.server.improvement.repository;

import java.util.Optional;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImprovementCountRepository extends JpaRepository<ImprovementCount, Long> {

    Optional<ImprovementCount> findByImprovementId(Long improvementID);

    void deleteByImprovementId(Long improvementId);

    @Query("SELECT i.viewCount FROM ImprovementCount i WHERE i.id = :improvementId")
    int findViewCountById(@Param("improvementId") Long improvementId);

    @Modifying
    @Query("UPDATE ImprovementCount i SET i.viewCount = :viewCount WHERE i.id = :improvementId")
    void updateViewCount(@Param("improvementId") Long improvementId, @Param("viewCount") int viewCount);
}
