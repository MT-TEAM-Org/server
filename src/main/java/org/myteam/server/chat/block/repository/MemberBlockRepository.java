package org.myteam.server.chat.block.repository;

import org.myteam.server.chat.block.domain.MemberBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberBlockRepository extends JpaRepository<MemberBlock, Long>, MemberBlockQueryRepository {
    boolean existsByBlockerPublicIdAndBlockedPublicId(UUID blocker, UUID blocked);
    Optional<MemberBlock> findByBlockerPublicIdAndBlockedPublicId(UUID blocker, UUID blocked);
}
