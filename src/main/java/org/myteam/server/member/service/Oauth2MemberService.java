package org.myteam.server.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.controller.response.Oauth2MemberResponse;
import org.myteam.server.member.repository.Oauth2MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.myteam.server.global.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Oauth2MemberService {
    private final Oauth2MemberRepository oauth2MemberRepository;

    public Oauth2MemberResponse getByEmail(String email) {
        return oauth2MemberRepository.findByEmail(email)
                .map(Oauth2MemberResponse::new)
                .orElseThrow(() -> new PlayHiveException(RESOURCE_NOT_FOUND, email + " 는 존재하지 않는 이메일 입니다"));
    }
}
