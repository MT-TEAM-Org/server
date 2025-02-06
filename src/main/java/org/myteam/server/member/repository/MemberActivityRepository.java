package org.myteam.server.member.repository;

import org.myteam.server.member.entity.MemberActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberActivityRepository extends JpaRepository<MemberActivity, Long> {
    Optional<MemberActivity> findByMemberPublicId(UUID publicId);
}