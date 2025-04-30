package org.myteam.server.match.match.repository;

import java.time.LocalDateTime;

import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
	@Query("SELECT m.startTime "
		+ "FROM p_match m "
		+ "WHERE FUNCTION('DATE', m.startTime) = FUNCTION('DATE', :today) "
		+ "AND m.category = :category "
		+ "ORDER BY m.startTime ASC "
		+ "LIMIT 1")
	LocalDateTime findMostRecentMatchStartTime(@Param("today") LocalDateTime today,
		@Param("category") MatchCategory category);
}
