//package org.myteam.server.improvement.repository;
//
//import org.myteam.server.improvement.domain.ImprovementReplyRecommend;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//import java.util.UUID;
//
//@Repository
//public interface ImprovementReplyRecommendRepository extends JpaRepository<ImprovementReplyRecommend, Long> {
//
//    Optional<ImprovementReplyRecommend> findByImprovementReplyIdAndMemberPublicId(Long improvementReplyId, UUID memberPublicId);
//
//    void deleteByImprovementReplyIdAndMemberPublicId(Long improveReplyId, UUID memberPublicId);
//}
