package org.myteam.server.member.service;

import java.util.UUID;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SecurityReadService {
	private final MemberJpaRepository memberRepository;



	public Member getMember() {
		return memberRepository.findByPublicId(getAuthenticatedUser().getPublicId())
				.orElseThrow(() -> new PlayHiveException(ErrorCode.USER_NOT_FOUND));
	}

	public Optional<Member> getOptionalMember() {
		return memberRepository.findByPublicId(getAuthenticatedUser().getPublicId());
	}

	private CustomUserDetails getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
			return (CustomUserDetails) authentication.getPrincipal();
		}
		throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
	}

	//로그인을 했던 안했던 통과해야하는 기능에 사용
	public UUID getAuthenticatedPublicId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
			return ((CustomUserDetails)authentication.getPrincipal()).getPublicId();
		}
		return null;
	}

}
