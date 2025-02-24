package org.myteam.server.improvement.repository;

import org.myteam.server.improvement.domain.Improvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImprovementRepository extends JpaRepository<Improvement, Long> {

}
