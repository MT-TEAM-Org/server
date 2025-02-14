package org.myteam.server.mypage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.domain.GenderType;
import org.myteam.server.member.domain.validator.MemberValidator;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.SecurityReadService;
import org.myteam.server.mypage.dto.request.MyPageRequest;
import org.myteam.server.profile.dto.request.ProfileRequestDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    private final SecurityReadService securityReadService;
    private final PasswordEncoder passwordEncoder;
    private final MemberJpaRepository memberJpaRepository;
    private final MemberValidator memberValidator;

    public void updateMemberInfo(MyPageRequest.MyPageUpdateRequest request) {
        Member member = securityReadService.getMember();

        if (!member.verifyOwnEmail(request.getEmail())) {
            throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
        }

        member.update(new ProfileRequestDto.MemberUpdateRequest(request.getEmail(), request.getPassword(), request.getTel(), request.getNickname()), passwordEncoder);

        if (request.getBirthDate() != null) {
            memberValidator.validateBirthDate(request.getBirthDate());
            member.updateBirthDate(request.getBirthDate());
        }

        if (request.getGenderType() != null) {
            GenderType.validateGender(request.getGenderType());
            member.updateGender(request.getGenderType());
        }

        memberJpaRepository.save(member);
        log.info("저장되었습니다.");
    }
}
