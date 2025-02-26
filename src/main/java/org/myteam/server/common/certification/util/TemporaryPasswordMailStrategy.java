package org.myteam.server.common.certification.util;

import lombok.extern.slf4j.Slf4j;
import org.myteam.server.common.mail.service.AbstractMailSender;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Slf4j
@Component
public class TemporaryPasswordMailStrategy extends AbstractMailSender {

    private final int PASSWORD_LENGTH = 10;
    private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final MemberJpaRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public TemporaryPasswordMailStrategy(JavaMailSender javaMailSender,
                                         MemberJpaRepository memberRepository,
                                         PasswordEncoder passwordEncoder) {
        super(javaMailSender);
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected String getSubject() {
        return "임시 비밀번호 안내";
    }

    @Override
    protected String getBody(String email) {
        String tempPassword = generateRandomPassword(email);
        return buildTemporaryPasswordEmailContent(tempPassword);
    }

    private String generateRandomPassword(String email) {
        SecureRandom random;
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            log.warn("SecureRandom.getInstanceStrong() 사용 불가, 기본 SecureRandom 사용");
            random = new SecureRandom();
        }

        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        String tempPassword = password.toString();
        log.info("랜덤 비밀번호 생성: {}", tempPassword);

        memberRepository.findByEmail(email).ifPresent(member -> {
            String encodedPassword = passwordEncoder.encode(tempPassword);
            member.updatePassword(encodedPassword);
            memberRepository.save(member); // DB 저장
            log.info("사용자 {}의 비밀번호가 임시 비밀번호로 변경됨", email);
        });

        return tempPassword;
    }

    public String buildTemporaryPasswordEmailContent(String tempPassword) {
        // 이메일 본문 생성
        String body = "<h3>임시 비밀번호 안내</h3>";
        body += "<p>귀하의 임시 비밀번호는 다음과 같습니다.</p>";
        body += "<h2 style=\"color: red;\">" + tempPassword + "</h2>";
        body += "<p>로그인 후 반드시 비밀번호를 변경해주세요.</p>";
        body += "<p style=\"color: #555555; font-size: 12px; text-align: left; margin-top: 20px;\">";
        body += "</p>";
        return body;
    }
}
