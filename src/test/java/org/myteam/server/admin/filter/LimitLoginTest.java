package org.myteam.server.admin.filter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LimitLoginTest extends IntegrationTestSupport {


    Member admin;
    Member member;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setting(){
        admin=createAdmin(1);
        member=createOAuthMember(3);

        String encode=passwordEncoder.encode(admin.getPassword());

        admin.updatePassword(encode);

        memberJpaRepository.save(admin);

        memberJpaRepository.save(member);
    }


    @Test
    @DisplayName("관리자 로그인 10회 실패시 계정이 잠기는지 테스트")
    void testLimitLogin() throws Exception{

        given(redisService.isAllowed("LOGIN_ADMIN","test1@test.com"))
                .willReturn(false);
        given(redisService.getRequestCount("LOGIN_ADMIN","test1@test.com"))
                .willReturn(10);

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


        Optional<Member> member=memberJpaRepository.findByEmail(admin.getEmail());
        assertThat(member.get().getStatus()).isEqualTo(MemberStatus.INACTIVE);


        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2))
                .andDo(print())
                .andExpect(status().isForbidden());



    }

    @Test
    @DisplayName("잘못된 계정 즉 없는 회원 이던가 혹은 회원 타입이 local인경우 실패 확인")
    void testingNoMemberLogin() throws Exception{

        //계정이 없는케이스
        String requestBody3 = """
            {
                "username": "test2@test.com",
                "password": "1234"
            }
        """;
        //oauth 유저가 로그인 시도시
        String requestBody4 = """
            {
                "username": "test3@test.com",
                "password": "1234"
            }
        """;

        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody3))
                .andDo(print())
                .andExpect(status().isUnauthorized());


        mockMvc.perform(post("/api/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody4))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }



}
