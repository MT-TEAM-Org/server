package org.myteam.server.auth.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.myteam.server.auth.dto.LoginRequestDto;
import org.myteam.server.auth.dto.SignupRequestDto;
import org.myteam.server.auth.dto.TokenResponseDto;
import org.myteam.server.global.jwt.JwtProvider;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public TokenResponseDto login(LoginRequestDto requestDto) {

        Member member = memberRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (!passwordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            throw new RuntimeException("Password not matched");
        }

        String accessToken = jwtProvider.generateToken(Duration.ofHours(1), member.getPublicId(), member.getRole());
        String refreshToken = jwtProvider.generateToken(Duration.ofDays(7), member.getPublicId(), member.getRole());

        return TokenResponseDto.of(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponseDto signup(SignupRequestDto requestDto) {
        Member member = requestDto.toEntity(passwordEncoder);
        memberRepository.save(member);

        String accessToken = jwtProvider.generateToken(Duration.ofHours(1), member.getPublicId(), member.getRole());
        String refreshToken = jwtProvider.generateToken(Duration.ofDays(7), member.getPublicId(), member.getRole());

        return TokenResponseDto.of(accessToken, refreshToken);

    }
}
