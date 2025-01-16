package org.myteam.server.member.repository;

import org.myteam.server.member.entity.Oauth2Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Oauth2MemberJpaRepository extends JpaRepository<Oauth2Member, Long> {
    Optional<Oauth2Member> findByEmail(String email);
}
