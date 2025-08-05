package org.myteam.server.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.admin.repository.AdminDashBoardRepository;
import org.myteam.server.admin.service.AdminImprovementService;
import org.myteam.server.admin.service.AdminInquiryService;
import org.myteam.server.admin.service.ContentSearchService;
import org.myteam.server.admin.utill.enums.StaticDataType;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.global.domain.Category;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.Report;
import org.myteam.server.report.domain.ReportType;
import org.myteam.server.support.IntegrationTestSupport;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.myteam.server.admin.dto.response.AdminDashBoardResponseDto.*;

public class AdminReadCheckTest extends IntegrationTestSupport {


    @Autowired
    AdminDashBoardRepository adminDashBoardRepository;
    @Autowired
    AdminInquiryService adminInquiryService;
    @Autowired
    AdminImprovementService adminImprovementService;


    @Autowired
    ContentSearchService contentSearchService;
    Member admin;
    Improvement improvement;
    Inquiry inquiry;

    Board board;
    Report report;

    @BeforeEach
    public void setting(){

        admin=createAdmin(0);
        Member member=createMember(0);
        board=createBoard(admin, Category.BASEBALL, CategoryType.FREE,"title",
                "zzzz");
        report=createReport(admin,member, BanReason.SEXUAL_CONTENT, ReportType.BOARD,board.getId());
        improvement=createImprovement(admin,false);
        inquiry=createInquiry(admin);
    }

    @DisplayName("읽지않음이 정상 처리되는지")
    void testNotRead(){
        when(redisService.AdminReadCheck(admin.getPublicId().toString()
                , StaticDataType.Inquiry,inquiry.getId()))
                .thenReturn(false);
        when(redisService.AdminReadCheck(admin.getPublicId().toString()
                , StaticDataType.Improvement,improvement.getId()))
                .thenReturn(false);
        Map<String,List<ResponseLatestData>> responseLatestData=adminDashBoardRepository.getLatestData();
        responseLatestData.keySet().stream().forEach(
                x->{
                    List<ResponseLatestData> responseLatestDataList=
                            responseLatestData.get((String) x);

                    responseLatestDataList.stream()
                            .forEach(y->{
                                assertThat(y.getCheckRead()).isFalse();
                            });
                }
        );

    }
    @DisplayName("읽음으로 정상 처리되는지")
    void testRead(){
        when(redisService.AdminReadCheck(admin.getPublicId().toString()
                , StaticDataType.Inquiry,inquiry.getId()))
                .thenReturn(true);
        when(redisService.AdminReadCheck(admin.getPublicId().toString()
                , StaticDataType.Improvement,improvement.getId()))
                .thenReturn(true);
        Map<String,List<ResponseLatestData>> responseLatestData=adminDashBoardRepository.getLatestData();
        responseLatestData.keySet().stream().forEach(
                x->{
                    List<ResponseLatestData> responseLatestDataList=
                            responseLatestData.get((String) x);

                    responseLatestDataList.stream()
                            .forEach(y->{
                                assertThat(y.getCheckRead()).isFalse();
                            });
                }
        );

    }

    @DisplayName("content detail 서비스로 접근시 호출이잘되는가")
    @Test
    void testContentReadDetail(){

        contentSearchService.getContentDetail(board.getId(),StaticDataType.BOARD,report.getId(),"zz");
        verify(redisService).adminReadCheckUpdate(any(String.class),any(StaticDataType.class),
                any(Long.class));
    }


    @DisplayName("content detail 서비스로 접근시 호출이안되는가")
    @Test
    void testContentReadDetail2(){

        contentSearchService.getContentDetail(board.getId(),StaticDataType.BOARD,null,"zcxzc");
        contentSearchService.getContentDetail(board.getId(),StaticDataType.BOARD, report.getId(),null);
        contentSearchService.getContentDetail(board.getId(),StaticDataType.BOARD,null,null);
        verify(redisService,never()).adminReadCheckUpdate(any(String.class),any(StaticDataType.class),
                any(Long.class));
    }

    @DisplayName("detail 서비스로 접근시에 adminreadcheckupdate가 호출이되는가")
    @Test
    void testInquiryReadDetail(){
        adminInquiryService.getInquiryDetail(inquiry.getId(),"Dfdsf");
        verify(redisService).adminReadCheckUpdate(any(String.class),any(StaticDataType.class),
                any(Long.class));

    }
    @DisplayName("detail 서비스로 접근시에 adminreadcheckupdate가 호출이되는가")
    @Test
    void testImprovementReadDetail(){
        adminImprovementService.getImproveDetail(improvement.getId(),"check");
        verify(redisService).adminReadCheckUpdate(any(String.class),any(StaticDataType.class),
                any(Long.class));

    }
    @DisplayName("detail 서비스로 접근시에 adminreadcheckupdate가 호출이 안되는가")
    @Test
    void testNotReadDetail(){
        adminImprovementService.getImproveDetail(improvement.getId(),null);
        adminInquiryService.getInquiryDetail(inquiry.getId(),null);
        verify(redisService,never()).adminReadCheckUpdate(any(String.class),any(StaticDataType.class),
                any(Long.class));
    }
}
