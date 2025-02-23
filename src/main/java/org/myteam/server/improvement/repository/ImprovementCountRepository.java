package org.myteam.server.improvement.repository;

import org.myteam.server.improvement.domain.ImprovementCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImprovementCountRepository extends JpaRepository<ImprovementCount, Long> {

    Optional<ImprovementCount> findByImprovementId(Long improvementID);

    void deleteByImprovementId(Long improvementId);
}
