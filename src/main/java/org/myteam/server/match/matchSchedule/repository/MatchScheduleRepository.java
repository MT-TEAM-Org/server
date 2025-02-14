package org.myteam.server.match.matchSchedule.repository;

import org.myteam.server.match.matchSchedule.domain.MatchSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchScheduleRepository extends JpaRepository<MatchSchedule, Long> {
}
