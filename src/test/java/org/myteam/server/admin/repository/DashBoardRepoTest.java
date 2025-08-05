package org.myteam.server.admin.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.myteam.server.admin.entity.AdminContentChangeLog;
import org.myteam.server.admin.entity.AdminImproveChangeLog;
import org.myteam.server.admin.entity.AdminInquiryChangeLog;
import org.myteam.server.admin.entity.AdminMemberChangeLog;
import org.myteam.server.admin.service.AdminDashBoardService;
import org.myteam.server.admin.utill.enums.AdminControlType;
import org.myteam.server.admin.utill.enums.AdminDashBoardType;
import org.myteam.server.admin.utill.enums.DateType;
import org.myteam.server.admin.utill.enums.StaticDataType;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.security.jwt.JwtProvider;
import org.myteam.server.improvement.domain.ImportantStatus;
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
import static org.assertj.core.api.Assertions.*;
import static org.myteam.server.admin.dto.response.AdminDashBoardResponseDto.ResponseLatestData;
import static org.myteam.server.admin.dto.response.AdminDashBoardResponseDto.ResponseStatic;
import static org.myteam.server.global.security.jwt.JwtProvider.TOKEN_CATEGORY_ACCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                        AdminMemberChangeLog adminChangeLog = AdminMemberChangeLog
                                .builder()
                                .admin(admin)
                                .memberId(member.getPublicId())
                                .memberStatus(MemberStatus.WARNED)
                                .build();

                        AdminContentChangeLog adminChangeLog2 =AdminContentChangeLog
                                .builder()
                                .admin(admin)
                                .contentId(comment.getId())
                                .staticDataType(StaticDataType.COMMENT)
                                .adminControlType(AdminControlType.HIDDEN)
                                .build();

                        AdminImproveChangeLog adminImproveChangeLog=AdminImproveChangeLog
                                .builder()
                                .improvementStatus(ImprovementStatus.COMPLETED)
                                .admin(admin)
                                .contentId(improvement.getId())
                                .importantStatus(ImportantStatus.NORMAL)
                                .build();


                       AdminInquiryChangeLog adminInquiryChangeLog=
                                AdminInquiryChangeLog
                                        .builder()
                                        .isMember(true)
                                        .isAnswered(true)
                                        .contentId(inquiry.getId())
                                        .admin(admin)
                                        .build();

                        adminInquiryChangeLogRepo.save(adminInquiryChangeLog);
                        inquiry.updateAdminAnswered();
                        improvement.updateState(ImprovementStatus.COMPLETED);
                        inquiryRepository.save(inquiry);
                        improvementRepository.save(improvement);
                        adminMemberChangeLogRepo.save(adminChangeLog);
                        adminContentChangeLogRepo.save(adminChangeLog2);
                        adminImproveChangeLogRepo.save(adminImproveChangeLog);
                    }
                    else {
                        report = createReport(member, member, BanReason.ETC, ReportType.COMMENT, comment.getId());
                        member.updateDeleteAt(dates.get(2));
                        memberJpaRepository.save(member);
                        createMemberAccess(member, dates.get(2));
                        AdminMemberChangeLog adminChangeLog = AdminMemberChangeLog
                                .builder()
                                .admin(admin)
                                .memberId(member.getPublicId())
                                .memberStatus(MemberStatus.INACTIVE)
                                .build();

                        AdminContentChangeLog adminChangeLog2 = AdminContentChangeLog
                                .builder()
                                .admin(admin)
                                .contentId(board.getId())
                                .staticDataType(StaticDataType.BOARD)
                                .adminControlType(AdminControlType.HIDDEN)
                                .build();


                        AdminImproveChangeLog adminImproveChangeLog=AdminImproveChangeLog
                                .builder()
                                .improvementStatus(ImprovementStatus.PENDING)
                                .admin(admin)
                                .contentId(improvement.getId())
                                .importantStatus(ImportantStatus.NORMAL)
                                .build();


                        AdminInquiryChangeLog adminInquiryChangeLog=
                                AdminInquiryChangeLog
                                        .builder()
                                        .isMember(false)
                                        .isAnswered(false)
                                        .contentId(inquiry.getId())
                                        .admin(admin)
                                        .build();

                        adminInquiryChangeLogRepo.save(adminInquiryChangeLog);
                        adminImproveChangeLogRepo.save(adminImproveChangeLog);
                        adminMemberChangeLogRepo.save(adminChangeLog);
                        adminContentChangeLogRepo.save(adminChangeLog2);
                    }
                });
    }

        @Test
        @DisplayName("문의 개선 건의사항 세부 통계테스트")
        void testGetInquiryImprovementTest(){

            List<ResponseStatic> responseStatics=adminDashBoardService.getStaticData(AdminDashBoardType.InquiryBoard,DateType.Day);


            assertThat(responseStatics.size()).isEqualTo(5);

            assertThat(responseStatics.get(0).getStaticDataName()).isEqualTo("Inquiry");
            assertThat(responseStatics.get(0).getCurrentCount()).isEqualTo(10);
            assertThat(responseStatics.get(0).getPastCount()).isEqualTo(0);
            assertThat(responseStatics.get(0).getPercent()).isEqualTo(100);

            assertThat(responseStatics.get(1).getStaticDataName()).isEqualTo("InquiryPending");
            assertThat(responseStatics.get(1).getCurrentCount()).isEqualTo(5);
            assertThat(responseStatics.get(1).getPastCount()).isEqualTo(0);
            assertThat(responseStatics.get(1).getPercent()).isEqualTo(100);

            assertThat(responseStatics.get(2).getStaticDataName()).isEqualTo("InquiryComplete");
            assertThat(responseStatics.get(2).getCurrentCount()).isEqualTo(5);
            assertThat(responseStatics.get(2).getPastCount()).isEqualTo(0);
            assertThat(responseStatics.get(2).getPercent()).isEqualTo(100);

            assertThat(responseStatics.get(3).getStaticDataName()).isEqualTo("InquiryMember");
            assertThat(responseStatics.get(3).getCurrentCount()).isEqualTo(5);
            assertThat(responseStatics.get(3).getPastCount()).isEqualTo(0);
            assertThat(responseStatics.get(3).getPercent()).isEqualTo(100);

            assertThat(responseStatics.get(4).getStaticDataName()).isEqualTo("InquiryNoMember");
            assertThat(responseStatics.get(4).getCurrentCount()).isEqualTo(5);
            assertThat(responseStatics.get(4).getPastCount()).isEqualTo(0);
            assertThat(responseStatics.get(4).getPercent()).isEqualTo(100);


            List<ResponseStatic> responseImproves=adminDashBoardService.getStaticData(AdminDashBoardType.ImprovementBoard,DateType.Day);

            assertThat(responseImproves.size()).isEqualTo(4);

            assertThat(responseImproves.get(0).getStaticDataName()).isEqualTo("Improvement");
            assertThat(responseImproves.get(0).getCurrentCount()).isEqualTo(10);
            assertThat(responseImproves.get(0).getPastCount()).isEqualTo(0);
            assertThat(responseImproves.get(0).getPercent()).isEqualTo(100);

            assertThat(responseImproves.get(1).getStaticDataName()).isEqualTo("ImprovementPending");
            assertThat(responseImproves.get(1).getCurrentCount()).isEqualTo(5);
            assertThat(responseImproves.get(1).getPastCount()).isEqualTo(0);
            assertThat(responseImproves.get(1).getPercent()).isEqualTo(100);

            assertThat(responseImproves.get(2).getStaticDataName()).isEqualTo("ImprovementReceived");
            assertThat(responseImproves.get(2).getCurrentCount()).isEqualTo(0);
            assertThat(responseImproves.get(2).getPastCount()).isEqualTo(0);
            assertThat(responseImproves.get(2).getPercent()).isEqualTo(0);

            assertThat(responseImproves.get(3).getStaticDataName()).isEqualTo("ImprovementComplete");
            assertThat(responseImproves.get(3).getCurrentCount()).isEqualTo(5);
            assertThat(responseImproves.get(3).getPastCount()).isEqualTo(0);
            assertThat(responseImproves.get(3).getPercent()).isEqualTo(100);
        }



    @Test
    @DisplayName("1일을 기준으로 데이터를 잘가져오는지 체크")
    void testGetDataByDay() {
        LocalDateTime now = LocalDateTime.now();


        List<ResponseStatic> responseStaticsDashBoard=adminDashBoardService.getStaticData(AdminDashBoardType.DashBoard,DateType.Day);
        List<ResponseStatic> responseStaticsMemberBoard=adminDashBoardService.getStaticData(AdminDashBoardType.MemberBoard,DateType.Day);
        List<ResponseStatic> responseStaticsContentBoard=adminDashBoardService.getStaticData(AdminDashBoardType.ContentBoard,DateType.Day);


        assertThat(responseStaticsDashBoard.size()).isEqualTo(8);

        assertThat(responseStaticsDashBoard.get(0).getCurrentStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(0).getPastStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(0).getCurrentCount()).isEqualTo(10);
        assertThat(responseStaticsDashBoard.get(0).getPastCount()).isEqualTo(0);
        assertThat(responseStaticsDashBoard.get(0).getTotCount()).isEqualTo(10);
        assertThat(responseStaticsDashBoard.get(0).getPercent()).isEqualTo(100);

        assertThat(responseStaticsDashBoard.get(1).getStaticDataName()).isEqualTo("Comment");
        assertThat(responseStaticsDashBoard.get(1).getCurrentStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(1).getPastStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(1).getCurrentCount()).isEqualTo(10);
        assertThat(responseStaticsDashBoard.get(1).getPastCount()).isEqualTo(0);
        assertThat(responseStaticsDashBoard.get(1).getTotCount()).isEqualTo(10);
        assertThat(responseStaticsDashBoard.get(1).getPercent()).isEqualTo(100);

        assertThat(responseStaticsDashBoard.get(2).getStaticDataName()).isEqualTo("ReportedBoard");
        assertThat(responseStaticsDashBoard.get(2).getCurrentStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(2).getPastStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(2).getCurrentCount()).isEqualTo(5);
        assertThat(responseStaticsDashBoard.get(2).getPastCount()).isEqualTo(0);
        assertThat(responseStaticsDashBoard.get(2).getTotCount()).isEqualTo(5);
        assertThat(responseStaticsDashBoard.get(2).getPercent()).isEqualTo(100);

        assertThat(responseStaticsDashBoard.get(3).getStaticDataName()).isEqualTo("ReportedComment");
        assertThat(responseStaticsDashBoard.get(3).getCurrentStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(3).getPastStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(3).getCurrentCount()).isEqualTo(5);
        assertThat(responseStaticsDashBoard.get(3).getPastCount()).isEqualTo(0);
        assertThat(responseStaticsDashBoard.get(3).getTotCount()).isEqualTo(5);
        assertThat(responseStaticsDashBoard.get(3).getPercent()).isEqualTo(100);

        assertThat(responseStaticsDashBoard.get(4).getStaticDataName()).isEqualTo("InquiryImprovement");
        assertThat(responseStaticsDashBoard.get(4).getCurrentStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(4).getPastStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(4).getCurrentCount()).isEqualTo(20);
        assertThat(responseStaticsDashBoard.get(4).getPastCount()).isEqualTo(0);
        assertThat(responseStaticsDashBoard.get(4).getTotCount()).isEqualTo(20);
        assertThat(responseStaticsDashBoard.get(4).getPercent()).isEqualTo(100);

        assertThat(responseStaticsDashBoard.get(5).getStaticDataName()).isEqualTo("MemberAccess");
        assertThat(responseStaticsDashBoard.get(5).getCurrentStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(5).getPastStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(5).getCurrentCount()).isEqualTo(5);
        assertThat(responseStaticsDashBoard.get(5).getPastCount()).isEqualTo(5);
        assertThat(responseStaticsDashBoard.get(5).getTotCount()).isEqualTo(10);
        assertThat(responseStaticsDashBoard.get(5).getPercent()).isEqualTo(0);

        assertThat(responseStaticsDashBoard.get(6).getStaticDataName()).isEqualTo("UserDeleted");
        assertThat(responseStaticsDashBoard.get(6).getCurrentStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(6).getPastStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(6).getCurrentCount()).isEqualTo(0);
        assertThat(responseStaticsDashBoard.get(6).getPastCount()).isEqualTo(5);
        assertThat(responseStaticsDashBoard.get(6).getTotCount()).isEqualTo(10);
        assertThat(responseStaticsDashBoard.get(6).getPercent()).isEqualTo(-100);

        assertThat(responseStaticsDashBoard.get(7).getStaticDataName()).isEqualTo("UserSignIn");
        assertThat(responseStaticsDashBoard.get(7).getCurrentStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(7).getPastStaticData().keySet().size()).isEqualTo(1);
        assertThat(responseStaticsDashBoard.get(7).getCurrentCount()).isEqualTo(11);
        assertThat(responseStaticsDashBoard.get(7).getPastCount()).isEqualTo(0);
        assertThat(responseStaticsDashBoard.get(7).getTotCount()).isEqualTo(11);
        assertThat(responseStaticsDashBoard.get(7).getPercent()).isEqualTo(100);



        assertThat(responseStaticsMemberBoard.size()).isEqualTo(5);

        assertThat(responseStaticsMemberBoard.get(3).getStaticDataName()).isEqualTo("UserWarned");
        assertThat(responseStaticsMemberBoard.get(3).getCurrentCount()).isEqualTo(5);
        assertThat(responseStaticsMemberBoard.get(3).getPastCount()).isEqualTo(0);
        assertThat(responseStaticsMemberBoard.get(3).getTotCount()).isEqualTo(5);
        assertThat(responseStaticsMemberBoard.get(3).getPercent()).isEqualTo(100);

        assertThat(responseStaticsMemberBoard.get(4).getStaticDataName()).isEqualTo("UserBanned");
        assertThat(responseStaticsMemberBoard.get(4).getCurrentCount()).isEqualTo(5);
        assertThat(responseStaticsMemberBoard.get(4).getPastCount()).isEqualTo(0);
        assertThat(responseStaticsMemberBoard.get(4).getTotCount()).isEqualTo(5);
        assertThat(responseStaticsMemberBoard.get(4).getPercent()).isEqualTo(100);

        assertThat(responseStaticsContentBoard.size()).isEqualTo(6);

        assertThat(responseStaticsContentBoard.get(4).getStaticDataName()).isEqualTo("HideBoard");
        assertThat(responseStaticsContentBoard.get(4).getCurrentCount()).isEqualTo(5);
        assertThat(responseStaticsContentBoard.get(4).getPastCount()).isEqualTo(0);
        assertThat(responseStaticsContentBoard.get(4).getTotCount()).isEqualTo(5);
        assertThat(responseStaticsContentBoard.get(4).getPercent()).isEqualTo(100);

        assertThat(responseStaticsContentBoard.get(5).getStaticDataName()).isEqualTo("HideComment");
        assertThat(responseStaticsContentBoard.get(5).getTotCount()).isEqualTo(5);
        assertThat(responseStaticsContentBoard.get(5).getCurrentCount()).isEqualTo(5);
        assertThat(responseStaticsContentBoard.get(5).getPastCount()).isEqualTo(0);
        assertThat(responseStaticsContentBoard.get(5).getPercent()).isEqualTo(100);

    }

    @Test
    @DisplayName("최신 데이터 가져오기")
    void testGetLatestDate() {


        Map<String,List<ResponseLatestData>> responseMap = adminDashBoardService.getLatestData();
        assertThat(responseMap.keySet().size()).isEqualTo(3);
        List<ResponseLatestData> reports=responseMap.get("Report");
        List<ResponseLatestData> inquiry=responseMap.get("Inquiry");
        List<ResponseLatestData> improve=responseMap.get("Improvement");

        assertThat(reports.size()).isEqualTo(10);
        assertThat(inquiry.size()).isEqualTo(10);
        assertThat(improve.size()).isEqualTo(10);


    }

    @Test
    @DisplayName("요구되는 변수가 빠졋을떄 및 범위 밖의 다른값을 입력시 일어나는 에러체크")
    void testValueNullError() throws Exception {


        mockMvc.perform(get("/api/admin/data/static")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());


        mockMvc.perform(get("/api/admin/data/static?dateType=Day")
                        .contentType(MediaType.APPLICATION_JSON)

                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/admin/data/static?staticType=Dash&dateType=Day")
                        .contentType(MediaType.APPLICATION_JSON)

                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/admin/data/static?staticType=DashBoard&dateType=Day")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk());


    }

}
