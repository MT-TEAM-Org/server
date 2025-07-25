package org.myteam.server.admin.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.myteam.server.admin.entity.AdminChangeLog;
import org.myteam.server.admin.service.AdminDashBoardService;
import org.myteam.server.admin.utill.AdminControlType;
import org.myteam.server.admin.utill.DateType;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.report.domain.Report;
import org.myteam.server.report.domain.ReportType;
import org.myteam.server.support.IntegrationTestSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.myteam.server.admin.dto.AdminBashBoardRequestDto.RequestLatestData;
import static org.myteam.server.admin.dto.AdminBashBoardRequestDto.RequestStatic;
import static org.myteam.server.admin.dto.AdminDashBoardResponseDto.ResponseLatestData;
import static org.myteam.server.admin.dto.AdminDashBoardResponseDto.ResponseStatic;
import static org.myteam.server.global.security.jwt.JwtProvider.TOKEN_CATEGORY_ACCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class DashBoardRepoTest extends IntegrationTestSupport {


    private static DataSource testDataSource;
    @Autowired
    AdminDashBoardService adminDashBoardService;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    AdminChangeLogRepo adminChangeLogRepo;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    JPAQueryFactory queryFactory;
    Member admin;
    String accessToken;

    @BeforeAll
    static void setupH2CustomFunctions(@Autowired DataSource dataSource) {
        testDataSource = dataSource; //
        try (Connection conn = testDataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE ALIAS IF NOT EXISTS DATE_FORMAT FOR 'org.myteam.server.admin.utill.StaticUtil.dateFormat'");
            System.out.println("H2에 DATE_FORMAT 함수 별칭이 성공적으로 등록되었습니다.");
        } catch (SQLException e) {
            System.err.println("H2 함수 별칭 등록 중 오류 발생: " + e.getMessage());
            e.printStackTrace();

            throw new RuntimeException("Failed to register H2 DATE_FORMAT alias", e);
        }
    }


    @BeforeEach
    @DisplayName("뉴스는 필요없긴한대 그냥 각각 10개씩 생성")
    void createDate() {

        admin = createAdmin(1);

        accessToken = jwtProvider.generateToken(TOKEN_CATEGORY_ACCESS, Duration.ofDays(1),
                admin.getPublicId(), admin.getRole().name(),
                admin.getStatus().name());

        LocalDateTime now = LocalDateTime.now().with(LocalTime.MIDNIGHT);
        List<LocalDateTime> dates = List.of(now.plusDays(1L), now, now.minusDays(1L));

        IntStream.range(0, 10)
                .forEach(x -> {
                    String val = String.valueOf(x);
                    Member member = createMember(x);
                    Board board = createBoard(member, Category.BASEBALL, CategoryType.FREE, val, val);
                    Inquiry inquiry = createInquiry(member);
                    Improvement improvement = createImprovement(member, false);
                    News news = createNews(0, Category.BASEBALL, 0);
                    Comment comment = createNewsComment(news, member, val);

                    Report report;
                    if (x % 2 == 0) {
                        report = createReport(member, member, BanReason.ETC, ReportType.BOARD, board.getId());
                        member.updateDeleteAt(dates.get(0));
                        memberJpaRepository.save(member);
                        createMemberAccess(member, dates.get(1));
                        AdminChangeLog adminChangeLog = AdminChangeLog
                                .builder()
                                .admin(admin)
                                .memberId(member.getPublicId())
                                .memberStatus(MemberStatus.PENDING)
                                .build();

                        AdminChangeLog adminChangeLog2 = AdminChangeLog
                                .builder()
                                .admin(admin)
                                .contentId(comment.getId())
                                .staticDataType(StaticDataType.COMMENT)
                                .adminControlType(AdminControlType.HIDDEN)
                                .build();
                        inquiry.updateAdminAnswered();
                        improvement.updateState(ImprovementStatus.COMPLETED);
                        inquiryRepository.save(inquiry);
                        improvementRepository.save(improvement);
                        adminChangeLogRepo.save(adminChangeLog);
                        adminChangeLogRepo.save(adminChangeLog2);
                    }
                    else {
                        report = createReport(member, member, BanReason.ETC, ReportType.COMMENT, comment.getId());
                        member.updateDeleteAt(dates.get(2));
                        memberJpaRepository.save(member);
                        createMemberAccess(member, dates.get(2));
                        AdminChangeLog adminChangeLog = AdminChangeLog
                                .builder()
                                .admin(admin)
                                .memberId(member.getPublicId())
                                .memberStatus(MemberStatus.INACTIVE)
                                .build();

                        AdminChangeLog adminChangeLogA = AdminChangeLog
                                .builder()
                                .admin(admin)
                                .memberId(member.getPublicId())
                                .memberStatus(MemberStatus.INACTIVE)
                                .build();

                        AdminChangeLog adminChangeLog2 = AdminChangeLog
                                .builder()
                                .admin(admin)
                                .contentId(board.getId())
                                .staticDataType(StaticDataType.BOARD)
                                .adminControlType(AdminControlType.HIDDEN)
                                .build();
                        adminChangeLogRepo.save(adminChangeLog);
                        adminChangeLogRepo.save(adminChangeLogA);
                        adminChangeLogRepo.save(adminChangeLog2);
                    }
                });
    }

        @Test
        @DisplayName("문의 개선 건의사항 세부 통계테스트")
        void testGetInquiryImprovementTest(){
            RequestStatic InquiryFin= RequestStatic
                    .builder()
                    .staticDataType(StaticDataType.InquiryComplete)
                    .dateType(DateType.Day)
                    .build();

            RequestStatic InquiryNotFin = RequestStatic
                    .builder()
                    .staticDataType(StaticDataType.InquiryPending)
                    .dateType(DateType.Day)
                    .build();

            ResponseStatic responseStatic=adminDashBoardService.getStaticData(InquiryFin);
            ResponseStatic responseStatic1=adminDashBoardService.getStaticData(InquiryNotFin);

            assertThat(responseStatic.getCurrentCount()).isEqualTo(5);
            assertThat(responseStatic.getPastCount()).isEqualTo(0);
            assertThat(responseStatic.getPercent()).isEqualTo(100);

            assertThat(responseStatic1.getCurrentCount()).isEqualTo(5);
            assertThat(responseStatic1.getPastCount()).isEqualTo(0);
            assertThat(responseStatic1.getPercent()).isEqualTo(100);


            RequestStatic ImprovementFin= RequestStatic
                    .builder()
                    .staticDataType(StaticDataType.ImprovementComplete)
                    .dateType(DateType.Day)
                    .build();

            RequestStatic ImprovementPending = RequestStatic
                    .builder()
                    .staticDataType(StaticDataType.ImprovementPending)
                    .dateType(DateType.Day)
                    .build();
            ResponseStatic responseStaticComplete=adminDashBoardService.getStaticData(ImprovementFin);
            ResponseStatic responseStaticPending=adminDashBoardService.getStaticData(ImprovementPending);

            assertThat(responseStaticComplete.getCurrentCount()).isEqualTo(5);
            assertThat(responseStaticComplete.getPastCount()).isEqualTo(0);
            assertThat(responseStaticComplete.getPercent()).isEqualTo(100);

            assertThat(responseStaticPending.getCurrentCount()).isEqualTo(5);
            assertThat(responseStaticPending.getPastCount()).isEqualTo(0);
            assertThat(responseStaticPending.getPercent()).isEqualTo(100);

        }



    @Test
    @DisplayName("1일을 기준으로 데이터를 잘가져오는지 체크")
    void testGetDataByDay() {
        LocalDateTime now = LocalDateTime.now();

        RequestStatic requestStaticComment = RequestStatic
                .builder()
                .staticDataType(StaticDataType.ReportedComment)
                .dateType(DateType.Day)
                .build();
        RequestStatic requestStaticImprovement = RequestStatic
                .builder()
                .staticDataType(StaticDataType.ImprovementInquiry)
                .dateType(DateType.Day)
                .build();

        RequestStatic requestStaticBoard = RequestStatic
                .builder()
                .staticDataType(StaticDataType.ReportedBoard)
                .dateType(DateType.Day)
                .build();
        RequestStatic requestStaticSignIn = RequestStatic
                .builder()
                .staticDataType(StaticDataType.UserSignIn)
                .dateType(DateType.Day)
                .build();
        RequestStatic requestStaticDelete = RequestStatic
                .builder()
                .staticDataType(StaticDataType.UserDeleted)
                .dateType(DateType.Day)
                .build();
        RequestStatic requestStaticAccess = RequestStatic
                .builder()
                .staticDataType(StaticDataType.UserAccess)
                .dateType(DateType.Day)
                .build();

        RequestStatic requestStaticWarned = RequestStatic
                .builder()
                .staticDataType(StaticDataType.UserWarned)
                .dateType(DateType.Day)
                .build();

        RequestStatic requestStaticBanned = RequestStatic
                .builder()
                .staticDataType(StaticDataType.UserBanned)
                .dateType(DateType.Day)
                .build();

        RequestStatic requestStaticHiddenComment = RequestStatic
                .builder()
                .staticDataType(StaticDataType.HideComment)
                .dateType(DateType.Day)
                .build();

        RequestStatic requestStaticHiddenBoard = RequestStatic
                .builder()
                .staticDataType(StaticDataType.HideBoard)
                .dateType(DateType.Day)
                .build();


        ResponseStatic responseStaticBoard = adminDashBoardService.getStaticData(requestStaticBoard);
        ResponseStatic responseStaticComment = adminDashBoardService.getStaticData(requestStaticComment);
        ResponseStatic responseStaticDelete = adminDashBoardService.getStaticData(requestStaticDelete);
        ResponseStatic responseStaticSignIn = adminDashBoardService.getStaticData(requestStaticSignIn);
        ResponseStatic responseStaticImprovement = adminDashBoardService.getStaticData(requestStaticImprovement);
        ResponseStatic responseStaticUserAccess = adminDashBoardService.getStaticData(requestStaticAccess);
        ResponseStatic responseStaticWarned = adminDashBoardService.getStaticData(requestStaticWarned);
        ResponseStatic responseStaticBanned = adminDashBoardService.getStaticData(requestStaticBanned);
        ResponseStatic responseStaticHiddenComment = adminDashBoardService.getStaticData(requestStaticHiddenComment);
        ResponseStatic responseStaticHiddenBoard = adminDashBoardService.getStaticData(requestStaticHiddenBoard);


        assertThat(responseStaticBoard.getCurrentStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticBoard.getCurrentCount()).isEqualTo(5);
        assertThat(responseStaticBoard.getPastCount()).isEqualTo(0);
        assertThat(responseStaticBoard.getTotCount()).isEqualTo(5);
        assertThat(responseStaticBoard.getPercent()).isEqualTo(100);

        assertThat(responseStaticComment.getCurrentStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticComment.getCurrentCount()).isEqualTo(5);
        assertThat(responseStaticComment.getPastCount()).isEqualTo(0);
        assertThat(responseStaticComment.getTotCount()).isEqualTo(5);
        assertThat(responseStaticComment.getPercent()).isEqualTo(100);

        assertThat(responseStaticImprovement.getCurrentStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticImprovement.getCurrentCount()).isEqualTo(20);
        assertThat(responseStaticImprovement.getPastCount()).isEqualTo(0);
        assertThat(responseStaticImprovement.getTotCount()).isEqualTo(20);
        assertThat(responseStaticImprovement.getPercent()).isEqualTo(100);

        assertThat(responseStaticSignIn.getCurrentStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticSignIn.getCurrentCount()).isEqualTo(11);
        assertThat(responseStaticSignIn.getPastCount()).isEqualTo(0);
        assertThat(responseStaticSignIn.getTotCount()).isEqualTo(11);
        assertThat(responseStaticSignIn.getPercent()).isEqualTo(100);

        assertThat(responseStaticDelete.getCurrentStaticData().keySet().size()).isEqualTo(0);
        assertThat(responseStaticDelete.getCurrentCount()).isEqualTo(0);
        assertThat(responseStaticDelete.getPastCount()).isEqualTo(5);
        assertThat(responseStaticDelete.getTotCount()).isEqualTo(10);
        assertThat(responseStaticDelete.getPercent()).isEqualTo(-100);

        assertThat(responseStaticUserAccess.getCurrentStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticUserAccess.getCurrentCount()).isEqualTo(5);
        assertThat(responseStaticUserAccess.getPastCount()).isEqualTo(5);
        assertThat(responseStaticUserAccess.getTotCount()).isEqualTo(10);
        assertThat(responseStaticUserAccess.getPercent()).isEqualTo(0);

        assertThat(responseStaticWarned.getCurrentCount()).isEqualTo(5);
        assertThat(responseStaticWarned.getPercent()).isEqualTo(100);
        assertThat(responseStaticWarned.getPastCount()).isEqualTo(0);

        assertThat(responseStaticBanned.getCurrentCount()).isEqualTo(5);
        assertThat(responseStaticBanned.getPercent()).isEqualTo(100);
        assertThat(responseStaticBanned.getPastCount()).isEqualTo(0);

        assertThat(responseStaticHiddenBoard.getCurrentCount()).isEqualTo(5);
        assertThat(responseStaticHiddenBoard.getPercent()).isEqualTo(100);
        assertThat(responseStaticHiddenBoard.getPastCount()).isEqualTo(0);

        assertThat(responseStaticHiddenComment.getCurrentCount()).isEqualTo(5);
        assertThat(responseStaticHiddenComment.getPercent()).isEqualTo(100);
        assertThat(responseStaticHiddenComment.getPastCount()).isEqualTo(0);

        Map<String, Long> hashmap = responseStaticImprovement.getCurrentStaticData();

        hashmap.keySet().stream()
                .forEach(x -> {
                    assertThat(hashmap.get(x)).isEqualTo(20);
                });


        Map<String, Long> hashmap2 = responseStaticUserAccess.getCurrentStaticData();

        hashmap2.keySet().stream()
                .forEach(x -> {
                    assertThat(hashmap2.get(x)).isEqualTo(5);
                });

    }

    @Test
    @DisplayName("최신 데이터 가져오기")
    void testGetLatestDate() {


        RequestLatestData requestLatestDataInquiry = RequestLatestData.
                builder()
                .staticDataType(StaticDataType.Inquiry)
                .build();

        RequestLatestData requestLatestDataImproveMent = RequestLatestData.
                builder()
                .staticDataType(StaticDataType.Improvement)
                .build();


        RequestLatestData requestLatestDataReport = RequestLatestData.
                builder()
                .staticDataType(StaticDataType.Report)
                .build();


        List<ResponseLatestData> responseLatestDataInquiry = adminDashBoardService.getLatestData(requestLatestDataInquiry);
        List<ResponseLatestData> responseLatestDataImprovement = adminDashBoardService.getLatestData(requestLatestDataImproveMent);
        List<ResponseLatestData> responseLatestDataReport = adminDashBoardService.getLatestData(requestLatestDataReport);

        assertThat(responseLatestDataInquiry.size()).isEqualTo(10);
        assertThat(responseLatestDataImprovement.size()).isEqualTo(10);
        assertThat(responseLatestDataReport.size()).isEqualTo(10);

    }

    @Test
    @DisplayName("요구되는 변수가 빠졋을떄 및 범위 밖의 다른값을 입력시 일어나는 에러체크")
    void testValueNullError() throws Exception {

        String requestBodyWithAbsent = """
                    {
                        "dateType": "Day"
                    }
                """;

        String requestBodyWithWrongValue = """
                    {
                    
                        "staticDataType":"Inqy"
                        "dateType": "Day"
                    }
                """;

        String requestBodyWithAbsent2 = """
                    {
                    
                    }
                """;

        String requestBodyWithWrongValue2 = """
                    {       
                        "staticDataType":"Inqy"
                    }
                """;
        String requestBodyWithWrongDate = """
                    {       
                        "staticDataType":"Inquiry"
                        "dateType":"zzzz"
                    }
                """;

        mockMvc.perform(post("/api/admin/data/static")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyWithAbsent)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());


        mockMvc.perform(post("/api/admin/data/static")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyWithWrongValue)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());


        mockMvc.perform(post("/api/admin/data/latest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyWithWrongValue2)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());


        mockMvc.perform(post("/api/admin/data/latest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyWithAbsent2)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());


        mockMvc.perform(post("/api/admin/data/static")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyWithWrongDate)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
