package org.myteam.server.global.security.service;

import static org.myteam.server.member.domain.MemberType.LOCAL;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberJpaRepository userRepository;

    public CustomUserDetailsService(MemberJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String[] userData = username.split(">");

        if (userData[1].equals("ADMIN")) {
            log.info("관리자 로그인 CustomUserDetailsService 실행됨");
            log.info("username : {}", userData[0]);
            Optional<Member> memberOP = userRepository
                    .findByEmailAndTypeAndRole(userData[0], LOCAL, MemberRole.ADMIN);

            if (memberOP.isPresent()) {
                // 로컬 회원인지 소셜 회원인지 확인
                if (memberOP.get().getType() != LOCAL) {
                    log.warn("소셜 회원이 로컬 로그인 시도 중 - 거부됨");
                    return null;
                }
                log.info(" 관리자 유저가 존재합니다. 인증 처리 로직을 실행합니다.");
                return new CustomUserDetails(memberOP.get());
            }
            log.warn("사용자를 찾을수 없습니다.");
            throw new PlayHiveException(ErrorCode.USER_NOT_FOUND);
        } else {
            log.info("일반 로그인 CustomUserDetailsService 실행됨");
            log.info("username : {}", userData[0]);

            Optional<Member> memberOP = userRepository
                    .findByEmailAndTypeAndRole(userData[0], LOCAL, MemberRole.USER);

            if (memberOP.isPresent()) {
                // 로컬 회원인지 소셜 회원인지 확인
                if (memberOP.get().getType() != LOCAL) {
                    log.warn("소셜 회원이 로컬 로그인 시도 중 - 거부됨");
                    return null;
                }
                log.info("일반 유저가 존재합니다. 인증 처리 로직을 실행합니다.");
                return new CustomUserDetails(memberOP.get());
            }
            log.warn("사용자를 찾을수 없습니다.");
            throw new PlayHiveException(ErrorCode.USER_NOT_FOUND);

        }

    }
}