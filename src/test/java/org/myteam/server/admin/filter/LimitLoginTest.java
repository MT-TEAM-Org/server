package org.myteam.server.admin.filter;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.myteam.server.chat.info.domain.UserInfo;
import org.myteam.server.common.certification.mail.core.MailStrategy;
import org.myteam.server.common.certification.mail.domain.EmailType;
import org.myteam.server.common.certification.mail.factory.MailStrategyFactory;
import org.myteam.server.common.certification.mail.strategy.NotifyAdminSuspendGlobalStrategy;
import org.myteam.server.common.certification.mail.strategy.NotifyAdminSuspendStrategy;
import org.myteam.server.common.certification.service.SuspendMailSendService;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.global.util.redis.service.RedisUserInfoService;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.repository.MemberJpaRepository;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.MemberService;
import org.myteam.server.support.IntegrationTestSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.myteam.server.global.security.jwt.JwtProvider.TOKEN_CATEGORY_ACCESS;
import static org.myteam.server.global.security.jwt.JwtProvider.TOKEN_CATEGORY_REFRESH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LimitLoginTest extends IntegrationTestSupport {


    Member admin;
    Member oauth2Member;

    Member normalMember;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    
    @MockBean
    RedisUserInfoService redisUserInfoService;
    
    @Autowired
    JwtProvider jwtProvider;


    @Autowired
    SuspendMailSendService suspendMailSendService;
    
    @BeforeEach
    void setting(){
        admin=createAdmin(1);
        oauth2Member=createOAuthMember(3);
        normalMember=createMember(4);
        String adminEncode=passwordEncoder.encode(admin.getPassword());
        admin.updatePassword(adminEncode);

        String oauth2Encode=passwordEncoder.encode(oauth2Member.getPassword());
        oauth2Member.updatePassword(oauth2Encode);

        String normalEncode=passwordEncoder.encode(normalMember.getPassword());
        normalMember.updatePassword(normalEncode);

        memberJpaRepository.save(admin);

        memberJpaRepository.save(oauth2Member);

        memberJpaRepository.save(normalMember);
    }


    @Test
    @DisplayName("관리자 로그인 10회 실패시 계정이 잠기는지 확인")
    void testLimitLogin() throws Exception{

        given(redisService.isAdminLoginAllowed("LOGIN_ADMIN", "test1@test.com"))
                .willReturn(false);
        given(redisService.getRequestCount("LOGIN_ADMIN","test1@test.com"))
                .willReturn(10);

        NotifyAdminSuspendGlobalStrategy mockGlobalStrategy = mock(NotifyAdminSuspendGlobalStrategy.class);
        NotifyAdminSuspendStrategy mockSuspendStrategy = mock(NotifyAdminSuspendStrategy.class);

        when(mailStrategyFactory.getStrategy(EmailType.NOTIFY_ADMIN_SUSPEND_GLOBAL))
                .thenReturn(mockGlobalStrategy);
        when(mailStrategyFactory.getStrategy(EmailType.NOTIFY_ADMIN_SUSPEND))
                .thenReturn(mockSuspendStrategy);

        String requestBody = """
            {
                "username": "test1@test.com",
                "password": "123"
            }
        """;

        String requestBody2 = """
            {
                "username": "test1@test.com",
                "password": "1234"
            }
        """;
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        String email = "test1@test.com";
        verify(mockGlobalStrategy).send(email+">"+"127.0.0.1");
        verify(mockSuspendStrategy).send(email);

        Optional<Member> member=memberJpaRepository.findByEmail(admin.getEmail());
        assertThat(member.get().getStatus()).isEqualTo(MemberStatus.INACTIVE);

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2))
                .andDo(print())
                .andExpect(status().isForbidden());


    }


    @Test
    @DisplayName("잘못된 계정 즉 없는 회원 이던가 혹은 회원 타입이 local인경우 실패 확인" +
            "혹은 로그인 경로와 알맞지않는 유저가 로그인 시도시 실패 확인")
    void testingNoMemberLogin() throws Exception{

        String accessToken = jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofDays(1), admin.getPublicId(), MemberRole.ADMIN.name(),
                MemberStatus.ACTIVE.name());
        String refreshToken = jwtProvider.generateToken(TOKEN_CATEGORY_REFRESH, Duration.ofDays(30), admin.getPublicId(), MemberRole.ADMIN.name(),
                MemberStatus.ACTIVE.name());


        String accessToken2 = jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofDays(1), normalMember.getPublicId(), MemberRole.USER.name(),
                MemberStatus.ACTIVE.name());
        String refreshToken2 = jwtProvider.generateToken(TOKEN_CATEGORY_REFRESH, Duration.ofDays(30), normalMember.getPublicId(), MemberRole.USER.name(),
                MemberStatus.ACTIVE.name());


        willDoNothing().given(redisService)
                .putRefreshToken(admin.getPublicId(),refreshToken);
        willDoNothing().given(redisService)
                .putRefreshToken(normalMember.getPublicId(),refreshToken2);
       willDoNothing().given(redisUserInfoService)
               .saveUserInfo(accessToken,
                       new UserInfo(admin.getPublicId(), admin.getNickname(), ""));

        willDoNothing().given(redisUserInfoService)
                .saveUserInfo(accessToken2,
                        new UserInfo(normalMember.getPublicId(), normalMember.getNickname(), ""));
        
        


        //계정이 없는케이스
        String requestBodyNoMemberCase= """
            {
                "username": "test2@test.com",
                "password": "1234"
            }
        """;
        //oauth 유저가 로그인 시도시
        String requestBodyOauth2MemberCase = """
            {
                "username": "test3@test.com",
                "password": "1234"
            }
        """;



        //일반 유저가 로그인 시도시
        String requestBodyNormalCase = """
            {
                "username": "test4@test.com",
                "password": "1234"
            }
        """;


        //admin 유저가 로그인 시도시
        String requestBodyAdminCase = """
            {
                "username": "test1@test.com",
                "password": "1234"
            }
        """;

        //관리자 경로로 로그인 시도시
        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyNoMemberCase))
                .andDo(print())
                .andExpect(status().isUnauthorized());


        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyOauth2MemberCase))
                .andDo(print())
                .andExpect(status().isUnauthorized());


        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyNormalCase))
                .andDo(print())
                .andExpect(status().isUnauthorized());


        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyAdminCase))
                .andDo(print())
                .andExpect(status().isOk());





        //일반유저 경로 로그인 시도시
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyNoMemberCase))
                .andDo(print())
                .andExpect(status().isUnauthorized());


        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyOauth2MemberCase))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyAdminCase))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyNormalCase))
                .andDo(print())
                .andExpect(status().isOk());


    }



}
