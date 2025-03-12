package org.myteam.server.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.inquiry.service.InquiryCommentService;
import org.myteam.server.inquiry.service.InquiryService;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final MemberJpaRepository memberJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        Member user = Member.builder()
                .email("test@naver.com")
                .password(passwordEncoder.encode("123123a"))
                .tel("01012345678")
                .nickname("테스트 유저")
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .publicId(UUID.randomUUID())
                .status(MemberStatus.ACTIVE)
                .build();

        Member admin = Member.builder()
                .email("admin@naver.com")
                .password(passwordEncoder.encode("123123a"))
                .tel("01012345678")
                .nickname("테스트 관리자")
                .role(MemberRole.ADMIN)
                .type(MemberType.LOCAL)
                .publicId(UUID.randomUUID())
                .status(MemberStatus.ACTIVE)
                .build();

        memberJpaRepository.save(user);
        memberJpaRepository.save(admin);

        log.info("member data 초기화 완료");
    }
}