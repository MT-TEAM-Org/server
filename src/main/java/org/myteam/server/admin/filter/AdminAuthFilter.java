package org.myteam.server.admin.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.info.domain.UserInfo;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.global.util.redis.service.RedisService;
import org.myteam.server.global.util.redis.service.RedisUserInfoService;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.MemberReadService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;


import static org.myteam.server.global.security.jwt.JwtProvider.*;
import static org.myteam.server.global.security.jwt.JwtProvider.TOKEN_CATEGORY_REFRESH;

@RequiredArgsConstructor
@Slf4j
public class AdminAuthFilter extends UsernamePasswordAuthenticationFilter {

    /*private final JwtProvider jwtProvider;

    private final RedisService redisService;
    private final RedisUserInfoService redisUserInfoService;
    private final AuthenticationManager authenticationManager;



    public AdminAuthFilter(AuthenticationManager authenticationManager,
                                   JwtProvider jwtProvider,
                                   ApplicationEventPublisher eventPublisher,
                                   RedisService redisService,
                                   RedisUserInfoService redisUserInfoService) {
        setFilterProcessesUrl("/api/admin/login");
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.eventPublisher = eventPublisher;
        this.redisService = redisService;
        this.redisUserInfoService = redisUserInfoService;

    }




    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path=request.getServletPath();

        return !path.equals(loginPath);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> credentials = objectMapper.readValue(request.getInputStream(), Map.class);

        String account = credentials.get("username");
        String password = credentials.get("password");


        Member member=memberReadService.findByEmailAndRole(account,MemberRole.ADMIN);

        if(member==null){
            sendErrorResponse(response,HttpStatus.BAD_REQUEST,"없는 계정입니다.");
        }

        if(member.getStatus().name().equals(MemberStatus.INACTIVE.name())){

            sendErrorResponse(response,HttpStatus.BAD_REQUEST,"잠긴 계정입니다.");
        }


        if(!passwordEncoder.matches(member.getPassword(),passwordEncoder.encode(password))){

            if(redisService.isAllowed("ADMIN",member.getPublicId().toString())){

                int count=redisService.getRequestCount("ADMIN",member.getPublicId().toString());
                sendErrorResponse(response,HttpStatus.BAD_REQUEST,"남은 로그인 횟수::%s".formatted(String.valueOf(10-count)));

            }

            else{
                member.updateStatus(MemberStatus.INACTIVE);
                memberJpaRepository.save(member);
                sendErrorResponse(response,HttpStatus.BAD_REQUEST,"잠긴 계정입니다.");
            }

        }

        String accessToken = jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofDays(1),member.getPublicId(),member.getRole().name(),
                member.getStatus().name());
        String refreshToken = jwtProvider.generateToken(TOKEN_CATEGORY_REFRESH, Duration.ofDays(30),member.getPublicId(),member.getRole().name(),
                member.getStatus().name());




        log.debug("print accessToken: {}", accessToken);
        log.debug("print refreshToken: {}", refreshToken);
        log.debug("print role: {}",member.getRole().name());

        redisService.putRefreshToken(member.getPublicId(), refreshToken);
        redisUserInfoService.saveUserInfo(accessToken,
                new UserInfo(member.getPublicId(), member.getNickname(), member.getImgUrl()));
        log.info("member: {} caching user info", member.getPublicId());
        redisService.resetRequestCount("ADMIN",member.getPublicId().toString());
        response.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + accessToken);
        response.setStatus(HttpStatus.OK.value());


    }


    private void sendErrorResponse(HttpServletResponse response, HttpStatus httpStatus, String message) throws
            IOException {
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"message\":\"%s\",\"status\":\"%s\"}", message, httpStatus.name()));
    }*/
}
