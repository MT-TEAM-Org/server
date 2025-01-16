package org.myteam.server.member.repository;

import lombok.RequiredArgsConstructor;
import org.myteam.server.member.entity.Oauth2Member;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class Oauth2MemberRepositoryImpl implements Oauth2MemberRepository {

    private final Oauth2MemberJpaRepository oauth2MemberJpaRepository;

    @Override
    public Optional<Oauth2Member> findByEmail(String email) {
        return oauth2MemberJpaRepository.findByEmail(email);
    }
}
