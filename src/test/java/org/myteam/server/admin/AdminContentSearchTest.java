package org.myteam.server.admin;


import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.myteam.server.admin.entity.AdminChangeLog;
import org.myteam.server.admin.entity.AdminMemo;
import org.myteam.server.admin.repository.simpleRepo.AdminMemoRepository;
import org.myteam.server.admin.repository.ContentSearchRepository;
import org.myteam.server.admin.utill.AdminControlType;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.report.domain.ReportType;
import org.myteam.server.support.IntegrationTestSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.myteam.server.admin.dto.request.AdminMemoRequestDto.*;
import static org.myteam.server.admin.dto.request.ContentRequestDto.*;
import static org.myteam.server.admin.dto.response.ResponseContentDto.*;
import static org.myteam.server.admin.entity.QAdminChangeLog.adminChangeLog;
import static org.myteam.server.admin.entity.QAdminMemo.adminMemo;
import static org.myteam.server.global.security.jwt.JwtProvider.TOKEN_CATEGORY_ACCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class AdminContentSearchTest extends IntegrationTestSupport {


    @Autowired
    JPAQueryFactory queryFactory;
    @Autowired
    ContentSearchRepository contentSearchRepository;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AdminMemoRepository adminMemoRepository;

    @Autowired
    MockMvc mockMvc;

    Member m;
    Member admin;
    String accessToken;
    Board b;
    Comment c;

    @BeforeEach
    void settingBeforeTest() {
        admin = createAdmin(0);

        m = createMember(1);

        Member m2 = createOAuthMember(2);
        Member m3 = createOAuthMemberNonPending(3);

        News news = createNews(0, Category.BASEBALL, 0);

        b = createBoard(m, Category.ESPORTS, CategoryType.FREE, "z", "z");
        c = createNewsComment(news, m, "Z");

        createReport(m, m2, BanReason.POLITICAL_CONTENT, ReportType.COMMENT, c.getId());
        createReport(m, m2, BanReason.SEXUAL_CONTENT, ReportType.BOARD, b.getId());
        AdminMemo adminMemo = new AdminMemo("zzzz", admin, null, StaticDataType.COMMENT, c.getId());
        adminMemoRepository.save(adminMemo);

        Board b2 = createBoard(m2, Category.ESPORTS, CategoryType.FREE, "z", "z");
        Comment c2 = createNewsComment(news, m2, "Z");
        createReport(m2, m3, BanReason.ETC, ReportType.COMMENT, c2.getId());
        createReport(m2, m3, BanReason.ETC, ReportType.BOARD, b2.getId());


        Board b3 = createBoard(m3, Category.ESPORTS, CategoryType.FREE, "z", "z");
        Board b4 = createBoard(m3, Category.ESPORTS, CategoryType.FREE, "z", "z");
        Comment c3 = createNewsComment(news, m3, "Z");
        createReport(m3, m, BanReason.ETC, ReportType.COMMENT, c3.getId());
        createReport(m3, m, BanReason.ETC, ReportType.BOARD, b3.getId());


    }

    @Test
    void contentListTest() {

        RequestContentData requestContentDataNull = RequestContentData
                .builder()
                .offset(1)
                .build();


        List<ResponseContentSearch> responseContentPageNull = contentSearchRepository
                .getWhenDataTypeIsNullWithUnion(requestContentDataNull).getContent();

        assertThat(responseContentPageNull.size()).isEqualTo(7);


        requestContentDataNull = RequestContentData
                .builder()
                .reported(true)
                .offset(1)
                .build();
        responseContentPageNull = contentSearchRepository
                .getWhenDataTypeIsNullWithUnion(requestContentDataNull).getContent();

        assertThat(responseContentPageNull.size()).isEqualTo(6);


        requestContentDataNull = RequestContentData
                .builder()
                .reported(false)
                .offset(1)
                .build();

        responseContentPageNull = contentSearchRepository
                .getWhenDataTypeIsNullWithUnion(requestContentDataNull).getContent();

        assertThat(responseContentPageNull.size()).isEqualTo(1);


        RequestContentData requestContentData = RequestContentData
                .builder()
                .staticDataType(StaticDataType.BOARD)
                .reported(false)
                .offset(1)
                .build();

        List<ResponseContentSearch> responseContentPage = contentSearchRepository
                .getDataList(requestContentData).getContent();


        responseContentPage.stream().forEach(
                x -> {
                    assertThat(x.getReportCount()).isEqualTo(0);
                    assertThat(x.getReported()).isEqualTo("미신고");
                }
        );

        assertThat(responseContentPage.size()).isEqualTo(1);

        RequestContentData requestContentData2 = RequestContentData
                .builder()
                .staticDataType(StaticDataType.BOARD)
                .reported(null)
                .offset(1)
                .build();

        List<ResponseContentSearch> responseContentPage2 = contentSearchRepository.getDataList(requestContentData2).getContent();

        assertThat(responseContentPage2.size()).isEqualTo(4);

        RequestContentData requestContentData3 = RequestContentData
                .builder()
                .staticDataType(StaticDataType.COMMENT)
                .reported(true)
                .offset(1)
                .build();

        Page<ResponseContentSearch> responseContentPage3 = contentSearchRepository
                .getDataList(requestContentData3);

        assertThat(responseContentPage3.getContent().size()).isEqualTo(3);
        responseContentPage3
                .stream()
                .forEach(x -> {
                    assertThat(x.getReportCount()).isEqualTo(1);
                    assertThat(x.getReported()).isEqualTo("신고");
                });
    }

    @Test
    void getDetail() {
        RequestDetail requestDetail = RequestDetail
                .builder()
                .staticDataType(StaticDataType.BOARD)
                .contentId(b.getId())
                .build();


        ResponseDetail responseDetail = contentSearchRepository.getDetail(requestDetail);

        assertThat(responseDetail.getReportCount()).isEqualTo(1);
        assertThat(responseDetail.getRecommendCount()).isEqualTo(0);
        assertThat(responseDetail.getReported()).isEqualTo("신고");
        assertThat(responseDetail.getNickname()).isEqualTo("test1");


        RequestDetail requestDetail2 = RequestDetail
                .builder()
                .staticDataType(StaticDataType.COMMENT)
                .contentId(c.getId())
                .build();


        ResponseDetail responseDetail2 = contentSearchRepository.getDetail(requestDetail2);

        assertThat(responseDetail2.getReportCount()).isEqualTo(1);
        assertThat(responseDetail2.getRecommendCount()).isEqualTo(0);
        assertThat(responseDetail2.getReported()).isEqualTo("신고");
        assertThat(responseDetail2.getNickname()).isEqualTo("test1");
        assertThat(responseDetail2.getAdminMemoResponses().size()).isEqualTo(1);
        assertThat(responseDetail2.getAdminMemoResponses().get(0).getWriterName()).isEqualTo("test");

    }

    @Test
    @Transactional
    void createAdminChangeLogAndAdminMemoTest() {
        AdminMemoContentRequest adminMemoContentRequest= AdminMemoContentRequest
                .builder()
                .contentId(1L)
                .staticDataType(StaticDataType.COMMENT)
                .adminControlType(AdminControlType.SHOW)
                .content("테스트용")
                .build();
        contentSearchRepository.addAdminMemo(adminMemoContentRequest);

        List<AdminMemo> adminMemos = queryFactory.select(adminMemo)
                .from(adminMemo)
                .where(adminMemo.contentId.eq(1L), adminMemo.staticDataType.eq(StaticDataType.COMMENT))
                .fetch();
        List<AdminChangeLog> adminChangeLogs = queryFactory
                .select(
                        adminChangeLog
                )
                .from(adminChangeLog)
                .where(adminChangeLog.contentId.eq(1L)
                        , adminChangeLog.staticDataType.eq(StaticDataType.COMMENT))
                .fetch();

        assertThat(adminMemos.size()).isEqualTo(2);
        assertThat(adminChangeLogs.size()).isEqualTo(0);

    }

    @Test
    void getReportList() {

        RequestReportList requestReportList = RequestReportList
                .builder()
                .staticDataType(StaticDataType.COMMENT)
                .contentId(c.getId())
                .offset(1)
                .build();
        List<ResponseReportList> responseReportLists = contentSearchRepository.getReportList(requestReportList).getContent();

        assertThat(responseReportLists.get(0).getReportType()).isEqualTo("정치");
        assertThat(responseReportLists.get(0).getNickName()).isEqualTo("test1");

        RequestReportList requestReportList2 = RequestReportList
                .builder()
                .staticDataType(StaticDataType.BOARD)
                .contentId(b.getId())
                .offset(1)
                .build();
        List<ResponseReportList> responseReportLists2 = contentSearchRepository.getReportList(requestReportList2).getContent();

        assertThat(responseReportLists2.get(0).getReportType()).isEqualTo("풍기위반");
        assertThat(responseReportLists2.get(0).getNickName()).isEqualTo("test1");

    }

    @Test
    void missingArgsTest() throws Exception {
        String staticError = """
                    {
                        "contentId":"1"
                    }
                """;

        String offSet = """
                    {
                      
                    }
                """;
        String reportList = """
                    {
                              
                      
                    }
                """;
        accessToken = jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofDays(1),
                admin.getPublicId(), admin.getRole().name(),
                admin.getStatus().name());


        mockMvc.perform(post("/api/admin/content/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(staticError)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());


        mockMvc.perform(post("/api/admin/content/detail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(offSet)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());


        mockMvc.perform(post("/api/admin/content/reportList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reportList)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }


}
