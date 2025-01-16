package org.myteam.server.member.repository;

import org.myteam.server.member.entity.Oauth2Member;

import java.util.Optional;

public interface Oauth2MemberRepository {
    Optional<Oauth2Member> findByEmail(String email);
}
