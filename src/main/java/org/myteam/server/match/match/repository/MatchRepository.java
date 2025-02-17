package org.myteam.server.match.match.repository;

import org.myteam.server.match.match.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
}
