package org.myteam.server.auth.oauth2.service;


import lombok.extern.slf4j.Slf4j;
import org.myteam.server.auth.oauth2.constant.OAuth2ServiceProvider;
import org.myteam.server.auth.oauth2.dto.CustomOAuth2User;
import org.myteam.server.auth.oauth2.response.NaverResponse;
import org.myteam.server.auth.oauth2.response.OAuth2Response;
import org.myteam.server.global.util.PasswordUtil;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("CustomOAuth2UserService > Oauth2User Request: {}", oAuth2User.toString());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals(OAuth2ServiceProvider.NAVER)) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        Optional<Member> existDataOP = memberRepository.findByUsername(username);

        if (existDataOP.isPresent()) {
            // 유저가 이미 존재하는 경우 업데이트 처리
            log.info("CustomOAuth2UserService isPresentUser");
            Member existData = existDataOP.get();
            log.info("username : {}", username);
            log.info("email : {}", oAuth2Response.getEmail());
            log.info("name : {}", oAuth2Response.getName());

            existData.updateEmail(oAuth2Response.getEmail());

            memberRepository.save(existData);

            return new CustomOAuth2User(existData.getUsername(), existData.getRole());
        } else {
            // 신규 회원
            log.info("CustomOAuth2UserService create NewUser");
            log.info("username : {}", username);
            log.info("email : {}", oAuth2Response.getEmail());
            log.info("name : {}", oAuth2Response.getName());

            Member member = Member.builder()
                    .username(username)
                    .email(oAuth2Response.getEmail())
                    .password(PasswordUtil.generateRandomPassword())
                    .role("ROLE_USER")
                    .build();

            memberRepository.save(member);

            return new CustomOAuth2User(username, "ROLE_USER");
        }
    }
}
