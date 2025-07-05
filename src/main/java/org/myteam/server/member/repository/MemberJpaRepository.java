package org.myteam.server.member.repository;

import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByEmailAndType(String email, MemberType type);

    Optional<Member> findByEmailAndTypeAndRole(String email, MemberType type,MemberRole role);

    Optional<Member> findByPublicId(UUID publicId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByTel(String tel);
    List<Member> findByTel(String tel);
}
