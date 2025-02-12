package org.myteam.server.mypage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.domain.GenderType;
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

    public void updateMemberInfo(MyPageRequest.MyPageUpdateRequest request) {
        Member member = securityReadService.getMember();

        if (!member.getEmail().equals(request.getEmail())) {
            throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
        }

        member.update(new ProfileRequestDto.MemberUpdateRequest(request.getEmail(), request.getPassword(), request.getTel(), request.getNickname()), passwordEncoder);

        if (request.getBirthDate() != null) {
            validateBirthDate(request.getBirthDate());
            member.updateBirthDate(request.getBirthDate());
        }

        if (request.getGenderType() != null) {
            validateGender(request.getGenderType());
            member.updateGender(request.getGenderType());
        }

        memberJpaRepository.save(member);
        log.info("저장되었습니다.");
    }

    private void validateBirthDate(String birthDate) {
        if (birthDate.length() != 6) {
            throw new PlayHiveException(ErrorCode.INVALID_BIRTH_DATE);
        }

        int birthYear = Integer.parseInt(birthDate.substring(0, 2));
        int birthMonth = Integer.parseInt(birthDate.substring(2, 4));
        int birthDay = Integer.parseInt(birthDate.substring(4));

        if (birthMonth < 1 || birthMonth > 12) {
            throw new PlayHiveException(ErrorCode.INVALID_BIRTH_MONTH);
        }

        int[] maxDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (birthMonth == 2) { // 윤년 고려
            boolean isLeapYear = ((birthYear + 2000) % 4 == 0 && (birthYear + 2000) % 100 != 0) || ((birthYear + 2000) % 400 == 0);
            if (isLeapYear) {
                maxDays[1] = 29;
            }
        }

        if (birthDay < 1 || birthDay > maxDays[birthMonth - 1]) {
            throw new PlayHiveException(ErrorCode.INVALID_BIRTH_DAY);
        }
    }

    private void validateGender(GenderType genderType) {
        if (!genderType.equals(GenderType.F) && !genderType.equals(GenderType.M)) {
            throw new PlayHiveException(ErrorCode.INVALID_GENDER_TYPE);
        }
    }
}
