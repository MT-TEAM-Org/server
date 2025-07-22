package org.myteam.server.member.repository;

import org.myteam.server.member.entity.MemberAccess;
import org.myteam.server.member.entity.MemberActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberAccessRepository extends JpaRepository<MemberAccess,Long> {

    Optional<MemberAccess> findByPublicIdAndAccessTime(UUID publicId, LocalDateTime now);
}
