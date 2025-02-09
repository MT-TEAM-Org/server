package org.myteam.server.member.service;

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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SecurityReadService {
	private final MemberJpaRepository memberRepository;

	public Member getMember() {
		return memberRepository.findByPublicId(getAuthenticatedUser().getPublicId())
				.orElseThrow(() -> new PlayHiveException(ErrorCode.USER_NOT_FOUND));
	}

	private CustomUserDetails getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
			return (CustomUserDetails) authentication.getPrincipal();
		}
		throw new PlayHiveException(ErrorCode.UNAUTHORIZED);
	}

}
