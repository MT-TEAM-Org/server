package org.myteam.server.match.matchPredictionMember.repository;

import java.util.Optional;
import java.util.UUID;

import org.myteam.server.match.matchPredictionMember.domain.MatchPredictionMember;
import org.myteam.server.news.newsCountMember.domain.NewsCountMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchPredictionMemberRepository extends JpaRepository<MatchPredictionMember, Long> {

	Optional<MatchPredictionMember> findByMatchPredictionIdAndMemberPublicId(Long matchPredictionId, UUID memberId);

	void deleteByMatchPredictionIdAndMemberPublicId(Long matchPredictionId, UUID memberId);

}
