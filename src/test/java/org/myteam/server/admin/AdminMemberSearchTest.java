package org.myteam.server.admin;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.myteam.server.admin.repository.AdminMemberResearchRepo;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.repository.BoardRepository;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.comment.repository.CommentRepository;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.report.domain.ReportType;
import org.myteam.server.support.IntegrationTestSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.myteam.server.admin.dto.MemberSearchRequestDto.RequestMemberDetail;
import static org.myteam.server.admin.dto.MemberSearchRequestDto.RequestMemberSearch;
import static org.myteam.server.admin.dto.MemberSearchResponseDto.*;
import static org.myteam.server.global.security.jwt.JwtProvider.TOKEN_CATEGORY_ACCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminMemberSearchTest extends IntegrationTestSupport {

    @Autowired
    AdminMemberResearchRepo adminMemberResearchRepo;


    @Autowired
    CommentRepository commentRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    MockMvc mockMvc;

    Member m;


    Member admin;


    @Autowired
    JwtProvider jwtProvider;


    String accessToken;

    @BeforeEach
    void settingBeforeTest() {

        admin = createAdmin(0);

        m = createMember(1);
        Member m2 = createOAuthMember(2);
        Member m3 = createOAuthMemberNonPending(3);

        News news = createNews(0, Category.BASEBALL, 0);

        Board b = createBoard(m, Category.ESPORTS, CategoryType.FREE, "z", "z");
        Comment c = createNewsComment(news, m, "Z");
        createReport(m, m2, BanReason.ETC, ReportType.COMMENT, c.getId());
        createReport(m, m2, BanReason.ETC, ReportType.BOARD, b.getId());
        c.addRecommendCount();


        Board b2 = createBoard(m2, Category.ESPORTS, CategoryType.FREE, "z", "z");
        Comment c2 = createNewsComment(news, m2, "Z");
        createReport(m2, m3, BanReason.ETC, ReportType.COMMENT, c2.getId());
        createReport(m2, m3, BanReason.ETC, ReportType.BOARD, b2.getId());
        c2.addRecommendCount();

        Board b3 = createBoard(m3, Category.ESPORTS, CategoryType.FREE, "z", "z");
        Comment c3 = createNewsComment(news, m3, "Z");
        createReport(m3, m, BanReason.ETC, ReportType.COMMENT, c3.getId());
        createReport(m3, m, BanReason.ETC, ReportType.BOARD, b3.getId());
        c3.addRecommendCount();

        commentRepository.save(c);
        commentRepository.save(c2);
        commentRepository.save(c3);


    }


    @Test
    void repoTest() {

        RequestMemberSearch memberSearch = RequestMemberSearch
                .builder()
                .offset(1)
                .build();

        Page<ResponseMemberSearch> requestMemberSearchList = adminMemberResearchRepo.getMemberDataList(memberSearch);

        assertThat(requestMemberSearchList.getContent().size()).isEqualTo(3);

        requestMemberSearchList.get()
                .forEach(x -> {
                    assertThat(x.getBoardCount()).isEqualTo(1);
                    assertThat(x.getCommentCount()).isEqualTo(1);
                    assertThat(x.getRecommendCount()).isEqualTo(1);
                    assertThat(x.getReportCount()).isEqualTo(2);

                });
    }

    @Test
    void testingReportedList() {

        RequestMemberDetail requestMemberDetail = RequestMemberDetail
                .builder()
                .publicId(m.getPublicId())
                .offset(1)
                .build();

        Page<ResponseReportList> data = adminMemberResearchRepo.getMemberReportedList(requestMemberDetail);

        assertThat(data.getContent().size()).isEqualTo(2);
        assertThat(data.getContent().get(0).getReportedCount()).isEqualTo(1);

        assertThat(data.getContent().get(0).getReportType()).isEqualTo("게시글");
        assertThat(data.getContent().get(1).getReportType()).isEqualTo("댓글");
    }

    @Test
    void testingMemberDetail() {

        RequestMemberDetail requestMemberDetail = RequestMemberDetail
                .builder()
                .publicId(m.getPublicId())
                .offset(1)
                .build();

        ResponseMemberDetail responseMemberDetail = adminMemberResearchRepo.getMemberDetail(m.getPublicId());

        assertThat(responseMemberDetail.getCountBoard()).isEqualTo(1);
        assertThat(responseMemberDetail.getCountBoard()).isEqualTo(1);
        assertThat(responseMemberDetail.getReportedCount()).isEqualTo(2);
        assertThat(responseMemberDetail.getReportCount()).isEqualTo(2);
        assertThat(responseMemberDetail.getAdminMemoResponses().size()).isEqualTo(0);
    }


    @Test
    void apiParameterTest() throws Exception {


        String telError = """
                    {
                      "tel":"0105000000000"
                    }
                """;


        String publicId = """
                    {
                      
                    }
                """;
        String email = """
                    {
                      "email":"test0@test.com"
                      
                    }
                """;


        accessToken = jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofDays(1),
                admin.getPublicId(), admin.getRole().name(),
                admin.getStatus().name());


        mockMvc.perform(post("/api/admin/members/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(telError)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());


        mockMvc.perform(post("/api/admin/members/publicId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(publicId)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());


        mockMvc.perform(post("/api/admin/members/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(publicId)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());


        mockMvc.perform(put("/api/admin/members/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(email)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }


}


