package org.myteam.server.improvement.repository;

import org.myteam.server.improvement.domain.ImprovementCommentRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImprovementCommentRecommendRepository extends JpaRepository<ImprovementCommentRecommend, Long> {

    Optional<ImprovementCommentRecommend> findByImprovementCommentIdAndMemberPublicId(Long improvementCommentId, UUID memberPublicId);
    void deleteByImprovementCommentIdAndMemberPublicId(Long improvementCommentId, UUID memberPublicId);
}
