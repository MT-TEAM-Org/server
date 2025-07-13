package org.myteam.server.oauth2.service;

import static org.myteam.server.global.exception.ErrorCode.UNSUPPORTED_OAUTH_PROVIDER;
import static org.myteam.server.member.domain.MemberRole.USER;
import static org.myteam.server.oauth2.constant.OAuth2ServiceProvider.DISCORD;
import static org.myteam.server.oauth2.constant.OAuth2ServiceProvider.GOOGLE;
import static org.myteam.server.oauth2.constant.OAuth2ServiceProvider.KAKAO;
import static org.myteam.server.oauth2.constant.OAuth2ServiceProvider.NAVER;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ExistingUserAuthenticationException;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.security.util.PasswordUtil;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.entity.MemberActivity;
import org.myteam.server.member.repository.MemberActivityRepository;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.oauth2.dto.CustomOAuth2User;
import org.myteam.server.oauth2.response.DiscordResponse;
import org.myteam.server.oauth2.response.GoogleResponse;
import org.myteam.server.oauth2.response.KakaoResponse;
import org.myteam.server.oauth2.response.NaverResponse;
import org.myteam.server.oauth2.response.OAuth2Response;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberJpaRepository memberJpaRepository;
    private final MemberActivityRepository memberActivityRepository;

    public CustomOAuth2UserService(MemberJpaRepository memberJpaRepository,
                                   MemberActivityRepository memberActivityRepository) {
        this.memberJpaRepository = memberJpaRepository;
        this.memberActivityRepository = memberActivityRepository;
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }

        // 이메일 분리 (아이디와 도메인 부분)
        int atIndex = email.indexOf("@");
        String localPart = email.substring(0, atIndex); // 아이디 부분
        String domainPart = email.substring(atIndex);  // 도메인 부분

        // 아이디의 앞 3글자는 유지, 나머지는 '*'로 마스킹
        if (localPart.length() <= 3) {
            return localPart + domainPart;
        }

        String visiblePart = localPart.substring(0, 3); // 앞 3글자
        String maskedPart = "*".repeat(localPart.length() - 3); // 나머지는 '*'
        return visiblePart + maskedPart + domainPart;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("CustomOAuth2UserService > Oauth2User Request: {}", oAuth2User.toString());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = createOAuth2Response(registrationId, oAuth2User.getAttributes());

        // 지원하지 않는 PROVIDER
        if (oAuth2Response == null) {
            throw new PlayHiveException(UNSUPPORTED_OAUTH_PROVIDER);
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String providerId = createProviderId(oAuth2Response);
        Optional<Member> existDataOP = memberJpaRepository.findByEmailAndTypeAndStatus(
                oAuth2Response.getEmail(),
                MemberType.fromOAuth2Provider(oAuth2Response.getProvider()), MemberStatus.ACTIVE
        );

        if (existDataOP.isPresent()) {
            return handleExistingMember(existDataOP.get(), oAuth2Response);
        } else {
            // 신규 회원
            log.debug("CustomOAuth2UserService create NewUser");
            log.debug("providerId : {}", providerId);
            log.debug("email : {}", oAuth2Response.getEmail());
            log.debug("name : {}", oAuth2Response.getName());
            log.debug("MemberType.SOCIAL : {}", MemberType.LOCAL);
            log.debug("MemberRole.ROLE_USER : {}", USER);
            log.debug("Provider() : {}", oAuth2Response.getProvider());
            log.debug("registrationId : {}", registrationId);

            return createNewMember(oAuth2Response, providerId); // 우선 Oauth2 사용자 테이블에 저장;
        }
    }

    /**
     * 기존 존재하는 회원 처리
     */
    private OAuth2User handleExistingMember(Member member, OAuth2Response oAuth2Response) {
        Optional<Member> memberByEmailAndType = memberJpaRepository.findByEmailAndTypeAndStatus(
                oAuth2Response.getEmail(),
                MemberType.fromOAuth2Provider(oAuth2Response.getProvider()), MemberStatus.ACTIVE
        );

        if (memberByEmailAndType.isPresent()) {
            // 로그인한 소셜 정보가 이미 존재하는 경우 업데이트 처리
            log.debug("CustomOAuth2UserService isPresentUser");
            log.debug("providerId : {}", oAuth2Response.getProvider());
            log.debug("email : {}", oAuth2Response.getEmail());
            log.debug("name : {}", oAuth2Response.getName());
            log.debug("provider : {}", oAuth2Response.getProvider());
            log.debug("provider.name : {}", MemberType.fromOAuth2Provider(oAuth2Response.getProvider()).name());
            log.debug("provider.status : {}", member.getStatus().name());

            member.updateEmail(oAuth2Response.getEmail());

            return new CustomOAuth2User(member.getEmail(), member.getRole().name(), member.getPublicId(),
                    member.getStatus(), MemberType.fromOAuth2Provider(oAuth2Response.getProvider()));
        } else {
            // 로컬 이메일 계정으로 존재하는 유저
            throw new ExistingUserAuthenticationException(
                    "로컬 이메일 계정으로 이미 가입된 이메일입니다: " + maskEmail(oAuth2Response.getEmail())
            );
        }
    }

    @Transactional
    public OAuth2User createNewMember(OAuth2Response oAuth2Response, String providerId) {
        log.debug("Creating new member for providerId: {}", providerId);
        String password = PasswordUtil.generateRandomPassword();

        UUID publicId = UUID.randomUUID();
        Member newMember = Member.builder()
                .email(oAuth2Response.getEmail())
                .password(password)
                .role(USER)
                .tel(oAuth2Response.getTel())
                .nickname(oAuth2Response.getNickname())
                .publicId(publicId)
                .status(MemberStatus.PENDING)
                .type(MemberType.fromOAuth2Provider(oAuth2Response.getProvider()))
                .build();

        memberJpaRepository.save(newMember);
        memberJpaRepository.flush();

        MemberActivity memberActivity = new MemberActivity(newMember);
        memberActivityRepository.save(memberActivity);

        return new CustomOAuth2User(oAuth2Response.getEmail(), USER.name(), publicId, newMember.getStatus(),
                MemberType.fromOAuth2Provider(oAuth2Response.getProvider()));
    }

    /**
     * provider 값을 사용하여 providerId 를 생성한다.
     */
    private String createProviderId(OAuth2Response oAuth2Response) {
        return oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
    }

    /**
     * provider 를 기준으로 OAuth2Response 를 생성한다.
     */
    private OAuth2Response createOAuth2Response(String registrationId, Map<String, Object> attributes) {
        switch (registrationId) {
            case NAVER:
                return new NaverResponse(attributes);
            case GOOGLE:
                return new GoogleResponse(attributes);
            case DISCORD:
                return new DiscordResponse(attributes);
            case KAKAO:
                return new KakaoResponse(attributes);
            default:
                return null;
        }
    }
}