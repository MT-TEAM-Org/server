package org.myteam.server.match.matchPrediction.repository;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
public interface MatchPredictionLockRepository extends JpaRepository<MatchPrediction, Long> {

	@NotNull
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<MatchPrediction> findById(Long id);

}
