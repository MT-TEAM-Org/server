package org.myteam.server.match.matchPrediction.repository;

import java.util.Optional;

import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchPredictionRepository extends JpaRepository<MatchPrediction, Long> {

	Optional<MatchPrediction> findByMatchId(Long matchId);

}
