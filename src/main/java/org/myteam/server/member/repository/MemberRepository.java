package org.myteam.server.member.repository;

import org.myteam.server.member.entity.Member;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository {
    Optional<Member> findByNickname(String nickname);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByPublicId(UUID publicId);
    Member getByNickname(String nickname);
    Member getByEmail(String email);
    Member getByPublicId(UUID publicId);
}
