package org.myteam.server.match.matchReply.repository;

import org.myteam.server.match.matchReply.domain.MatchReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchReplyRepository extends JpaRepository<MatchReply, Long> {
}
