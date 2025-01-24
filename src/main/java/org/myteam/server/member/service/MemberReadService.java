package org.myteam.server.member.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberReadService {

    private final MemberRepository memberRepository;

    public Member findById(UUID publicId) {
        return memberRepository.findByPublicId(publicId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.USER_NOT_FOUND));
    }
}
