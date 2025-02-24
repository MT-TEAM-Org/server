package org.myteam.server.improvement.repository;

import org.myteam.server.improvement.domain.ImprovementComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImprovementCommentRepository extends JpaRepository<ImprovementComment, Long> {
}
