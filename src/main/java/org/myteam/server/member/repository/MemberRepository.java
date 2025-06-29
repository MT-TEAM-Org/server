package org.myteam.server.member.repository;


import lombok.RequiredArgsConstructor;
import org.checkerframework.common.value.qual.IntRangeFromPositive;
import org.myteam.server.member.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final PasswordEncoder passwordEncoder;

    private final MemberJpaRepository memberJpaRepository;



    public Member saveMember(Member member){

        member.updatePassword(passwordEncoder.encode(member.getPassword()));
        memberJpaRepository.save(member);
        return member;
    }
}
