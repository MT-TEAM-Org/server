package org.myteam.server.chat.block.repository;

import org.myteam.server.chat.block.domain.entity.MemberBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberBlockRepository extends JpaRepository<MemberBlock, Long>, MemberBlockQueryRepository {
    boolean existsByBlockerIdAndBlockedId(UUID blocker, UUID blocked);
    Optional<MemberBlock> findByBlockerIdAndBlockedId(UUID blocker, UUID blocked);
}
