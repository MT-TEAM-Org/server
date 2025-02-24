package org.myteam.server.improvement.repository;

import org.myteam.server.improvement.domain.ImprovementRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImprovementRecommendRepository extends JpaRepository<ImprovementRecommend, Long> {

    Optional<ImprovementRecommend> findByImprovementIdAndMemberPublicId(Long improvementId, UUID memberPublicId);

    void deleteByImprovementIdAndMemberPublicId(Long improvementId, UUID memberPublicId);
}
