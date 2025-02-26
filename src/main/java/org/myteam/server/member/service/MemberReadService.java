package org.myteam.server.member.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.dto.FindIdResponse;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.profile.dto.response.ProfileResponseDto.ProfileResponse;
import org.myteam.server.util.AESCryptoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.myteam.server.global.exception.ErrorCode.*;
import static org.myteam.server.global.security.jwt.JwtProvider.TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberReadService {

    private final MemberRepository memberRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final SecurityReadService securityReadService;
    private final JwtProvider jwtProvider;


    public Member findById(UUID publicId) {
        Member member = memberRepository.findByPublicId(publicId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.USER_NOT_FOUND));

        if (!member.verifyMemberStatus()) {
            throw new PlayHiveException(ErrorCode.INVALID_USER);
        }

        return member;
    }

    public Member findByEmail(String email) {
        return memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> new PlayHiveException(USER_NOT_FOUND));
    }

    public ProfileResponse getProfile() {
        Member member = securityReadService.getMember();

        return ProfileResponse.createProfileResponse(member);
    }

    public MemberResponse getByPublicId(UUID publicId) {
        return MemberResponse.createMemberResponse(memberRepository.getByPublicId(publicId));
    }

    public MemberResponse getByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(MemberResponse::new)
                .orElseThrow(() -> new PlayHiveException(RESOURCE_NOT_FOUND, email + " 는 존재하지 않는 이메일 입니다"));
    }

    public MemberResponse getByNickname(String nickname) {
        return memberRepository.findByNickname(nickname)
                .map(MemberResponse::new)
                .orElseThrow(() -> new PlayHiveException(RESOURCE_NOT_FOUND, nickname + " 는 존재하지 않는 닉네임 입니다"));
    }

    public List<Member> list() {
        return Optional.of(memberJpaRepository.findAll()).orElse(Collections.emptyList());
    }

    public boolean existsByNickname(String nickname) {
        return memberJpaRepository.existsByNickname(nickname);
    }

    /**
     * publicId 를 통한 사용자 아이디 조회
     *
     * @param publicId token 에 저장할 고유 번호
     * @return
     */
    public String getCurrentLoginUserEmail(UUID publicId) {
        MemberResponse response = getByPublicId(publicId);
        return response != null ? response.getEmail() : null;
    }

    /**
     * jwt 토큰에서 publicId 를 추출한다.
     *
     * @param authorizationHeader JWT 토큰
     * @return
     */
    public MemberResponse getAuthenticatedMember(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
            throw new PlayHiveException(NO_PERMISSION);
        }

        String accessToken = jwtProvider.getAccessToken(authorizationHeader);
        UUID publicId = jwtProvider.getPublicId(accessToken);
        return getByPublicId(publicId);
    }

    public MemberType getMemberTypeByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(Member::getType)
                .orElse(null);
    }

    public FindIdResponse findUserId(String phoneNumber) {
        if (phoneNumber.length() != 11 && phoneNumber.length() != 10) {
            throw new PlayHiveException(ErrorCode.INVALID_PHONE_NUMBER);
        }

        if (!memberJpaRepository.existsByTel(phoneNumber)) {
            throw new PlayHiveException(ErrorCode.USER_NOT_FOUND);
        }

        List<Member> memberList = memberJpaRepository.findByTel(phoneNumber);

        return FindIdResponse.createResponse(memberList);
    }
}
