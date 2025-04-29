package org.myteam.server.member.service;

import static org.myteam.server.global.domain.PlayHiveValidator.*;
import static org.myteam.server.global.exception.ErrorCode.*;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import org.myteam.server.common.certification.util.CertifyStorage;
import org.myteam.server.common.mail.domain.EmailType;
import org.myteam.server.common.mail.service.MailStrategy;
import org.myteam.server.common.mail.util.MailStrategyFactory;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.RedisService;
import org.myteam.server.member.controller.response.MemberResponse;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.dto.MemberRoleUpdateRequest;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.dto.MemberStatusUpdateRequest;
import org.myteam.server.member.dto.PasswordChangeRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.entity.MemberActivity;
import org.myteam.server.member.repository.MemberActivityRepository;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.repository.MemberRepository;
import org.myteam.server.oauth2.dto.AddMemberInfoRequest;
import org.myteam.server.profile.dto.request.ProfileRequestDto.MemberDeleteRequest;
import org.myteam.server.profile.dto.request.ProfileRequestDto.MemberUpdateRequest;
import org.myteam.server.util.AESCryptoUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

	private final MemberRepository memberRepository;
	private final MemberJpaRepository memberJpaRepository;
	private final SecurityReadService securityReadService;
	private final MemberActivityRepository memberActivityRepository;

	private final PasswordEncoder passwordEncoder;
	private final MailStrategyFactory mailStrategyFactory;
	private final CertifyStorage certifyStorage;
	private final RedisService redisService;

	/**
	 * 회원 가입
	 *
	 * @param memberSaveRequest
	 * @return
	 * @throws PlayHiveException
	 */
	public MemberResponse create(MemberSaveRequest memberSaveRequest) throws PlayHiveException {
		// 동일한 유저 이름 존재 검사
		Optional<Member> memberOP = memberJpaRepository.findByEmailAndType(memberSaveRequest.getEmail(), MemberType.LOCAL);

		if (memberOP.isPresent()) {
			// 아이디가 중복 되었다는 것
			throw new PlayHiveException(USER_ALREADY_EXISTS);
		}

		// 패스워드인코딩 + 회원 가입
		Member member = memberJpaRepository.save(new Member(memberSaveRequest, passwordEncoder));
		member.updateStatus(MemberStatus.ACTIVE);

		// MemberActivity 생성 및 연관 관계 설정
		MemberActivity memberActivity = new MemberActivity(member);  // 멤버와 연결된 활동 생성
		memberActivityRepository.save(memberActivity);  // DB에 저장

		// 메일 전송
		MailStrategy strategy = mailStrategyFactory.getStrategy(EmailType.WELCOME);
		strategy.send(member.getEmail());

		// dto 응답
		return MemberResponse.createMemberResponse(member);
	}

	/**
	 * 회원 프로필 수정
	 */
	public MemberResponse updateMemberProfile(MemberUpdateRequest memberUpdateRequest) {
		Member member = securityReadService.getMember();

		if (!member.getEmail().equals(memberUpdateRequest.getEmail())) {
			throw new PlayHiveException(NO_PERMISSION);
		}

		member.update(memberUpdateRequest, passwordEncoder);

		memberJpaRepository.save(member);
		log.info("회원 정보 수정 완료: {}", member.getPublicId());

		return MemberResponse.createMemberResponse(member);
	}

	/**
	 * 소셜 로그인 정보 추가
	 */
	public void addInfo(AddMemberInfoRequest request) {
		log.info("소셜로그인 추가정보: {}", request.getEmail());
		Optional<Member> existDataOP = memberJpaRepository.findByEmailAndType(
				request.getEmail(),
				request.getMemberType());

		if (!existDataOP.isPresent()) {
			log.warn("소셜로그인 유저 없음: {}", request.getEmail());
			throw new PlayHiveException(USER_NOT_FOUND);
		}

		Member member = existDataOP.get();
		if (member.getStatus() != MemberStatus.PENDING) {
			log.warn("소셜로그인 추가 정보 권한 없음: {}", request.getEmail());
			throw new PlayHiveException(UNAUTHORIZED);
		}

		member.update(request.getTel(), request.getNickname(), null);
		member.updateStatus(MemberStatus.ACTIVE);
		log.info("소셜 로그인 정보 수정: {}, nickname: {}, tel: {}",
				request.getEmail(), request.getNickname(), request.getTel());
	}

	/**
	 * 회원 탈퇴
	 */
	public void deleteMember() {
		Member member = securityReadService.getMember();

		/**
		 * 이거를 없애는 게 조금 이상한데, 일단 주석으로 남겨만 둠.
		 */
		// 자신의 계정인지 체크
//		boolean isOwnValid = member.verifyOwnEmail(memberDeleteRequest.getRequestEmail());
//		if (!isOwnValid)
//			throw new PlayHiveException(NO_PERMISSION);
//
//		// 비밀번호 일치 여부 확인
//		boolean isPWValid = member.validatePassword(memberDeleteRequest.getPassword(), passwordEncoder);
//		if (!isPWValid) throw new PlayHiveException(NO_PERMISSION);

		member.updateStatus(MemberStatus.INACTIVE);
		redisService.deleteRefreshToken(member.getPublicId());

		log.info("회원 탈퇴 처리 완료: {}", member.getPublicId());
	}

	@Transactional
	public MemberResponse updateRole(MemberRoleUpdateRequest memberRoleUpdateRequest) {
		boolean isValid = validate(memberRoleUpdateRequest);
		log.info("playHive updateRole isValid: {}", isValid);

		if (!isValid) {
			// 빈 Response 객체 반환
			throw new PlayHiveException(NO_PERMISSION, "인증 키와 패스워드가 일치하지 않습니다");
		}

		// 1. 동일한 유저 이름 존재 검사
		Optional<Member> memberOP = memberRepository.findByEmail(memberRoleUpdateRequest.getEmail());

		// 2. 아이디 미존재 체크
		if (memberOP.isEmpty()) {
			throw new PlayHiveException(ErrorCode.USER_NOT_FOUND);
		}

		Member member = memberOP.get();
		member.updateType(memberRoleUpdateRequest.getRole());

		// 5. dto 응답
		return MemberResponse.createMemberResponse(member);
	}

	@Transactional
	public void changePassword(String email, PasswordChangeRequest passwordChangeRequest) {
		Member findMember = memberRepository.getByEmail(email);
		boolean isEqual = passwordChangeRequest.checkPasswordAndConfirmPassword();
		if (!isEqual)
			throw new PlayHiveException(INVALID_PARAMETER, "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
		boolean isValid = findMember.validatePassword(passwordChangeRequest.getPassword(), passwordEncoder);
		if (!isValid)
			throw new PlayHiveException(UNAUTHORIZED, "현재 비밀번호가 일치하지 않습니다.");

		String password = passwordEncoder.encode(passwordChangeRequest.getNewPassword());

		findMember.updatePassword(password); // 비밀번호 변경
	}

	@Transactional
	public void updateStatus(String targetEmail, MemberStatusUpdateRequest memberStatusUpdateRequest) {
		log.info("토큰에서 추출된 이메일: {}, 상태를 변경할 대상 이메일: {}, 새로운 상태: {}",
			targetEmail, memberStatusUpdateRequest.getEmail(), memberStatusUpdateRequest.getStatus().name());

		// 요청자와 대상 사용자 정보 조회
		Member requester = memberRepository.getByEmail(targetEmail); // 요청자
		Member targetMember = memberRepository.getByEmail(memberStatusUpdateRequest.getEmail()); // 상태 변경 대상자

		// 1. 요청자가 본인의 상태를 변경하려는 경우
		if (requester.verifyOwnEmail(memberStatusUpdateRequest.getEmail())) {
			log.info("사용자가 자신의 상태를 변경 중: {}", targetEmail);
			if (!requester.getStatus().equals(MemberStatus.PENDING))
				throw new PlayHiveException(NO_PERMISSION); // PENDING 인 경우에만 본인의 상태 변경 가능하도록 처리
			requester.updateStatus(memberStatusUpdateRequest.getStatus());
			return;
		}

		// 2. 관리자가 다른 사용자의 상태를 변경하려는 경우
		if (requester.isAdmin()) {
			log.info("관리자가 상태를 변경 중: {}, 대상자: {}", targetEmail, memberStatusUpdateRequest.getEmail());
			targetMember.updateStatus(memberStatusUpdateRequest.getStatus());
			return;
		}

		// 3. 권한 없는 사용자가 다른 사용자의 상태를 변경하려고 시도한 경우
		log.warn("권한 없는 요청: 요청자 {}, 대상자 {}", targetEmail, memberStatusUpdateRequest.getEmail());
		throw new PlayHiveException(NO_PERMISSION, "상태 수정 권한이 없습니다.");
	}

	public void delete(String email) {
		Member member = memberJpaRepository.findByEmail(email)
			.orElseThrow(() -> new PlayHiveException(USER_NOT_FOUND));

		memberJpaRepository.delete(member);
	}

	public void generateTemporaryPassword(String email) {
		log.info("랜덤 비번 생성 시도: {}", email);
		boolean isValid = certifyStorage.isCertified(email);

		if (!isValid) {
			throw new PlayHiveException(ErrorCode.UNAUTHORIZED_EMAIL_ACCOUNT);
		}

		log.info("메일 전송 시작 - email: {}", email);
		MailStrategy strategy = mailStrategyFactory.getStrategy(EmailType.TEMPORARY_PASSWORD);
		strategy.send(email);
		log.info("임시 비밀번호 완료");
	}
}
