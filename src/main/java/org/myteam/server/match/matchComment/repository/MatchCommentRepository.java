package org.myteam.server.match.matchComment.repository;

import org.myteam.server.match.matchComment.domain.MatchComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchCommentRepository extends JpaRepository<MatchComment, Long> {
}
