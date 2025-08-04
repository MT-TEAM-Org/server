package org.myteam.server.admin;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.admin.dto.request.ImproveRequestDto.RequestImprovementDetail;
import org.myteam.server.admin.repository.AdminDashBoardRepository;
import org.myteam.server.admin.service.AdminImprovementService;
import org.myteam.server.admin.service.AdminInquiryService;
import org.myteam.server.admin.service.ContentSearchService;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.common.certification.service.InquiryAnsSendService;
import org.myteam.server.global.domain.Category;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.Report;
import org.myteam.server.report.domain.ReportType;
import org.myteam.server.support.IntegrationTestSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.myteam.server.admin.dto.request.AdminDashBoardRequestDto.*;
import static org.myteam.server.admin.dto.request.ContentRequestDto.*;
import static org.myteam.server.admin.dto.request.InquiryRequestDto.*;
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
        RequestLatestData requestLatestData=RequestLatestData
                .builder()
                .staticDataType(StaticDataType.Inquiry)
                .build();
        RequestLatestData requestLatestData2=RequestLatestData
                .builder()
                .staticDataType(StaticDataType.Improvement)
                .build();
        List<ResponseLatestData> responseLatestData=adminDashBoardRepository.getLatestData(requestLatestData);
        List<ResponseLatestData> responseLatestData2=adminDashBoardRepository.getLatestData(requestLatestData2);
        responseLatestData.stream()
                .forEach(x->{
                    Assertions.assertThat(x.getCheckRead()).isFalse();
                });
        responseLatestData2.stream()
                .forEach(x->{
                    Assertions.assertThat(x.getCheckRead()).isFalse();
                });
    }
    @DisplayName("읽음으로 정상 처리되는지")
    void testRead(){
        when(redisService.AdminReadCheck(admin.getPublicId().toString()
                , StaticDataType.Inquiry,inquiry.getId()))
                .thenReturn(true);
        when(redisService.AdminReadCheck(admin.getPublicId().toString()
                , StaticDataType.Improvement,improvement.getId()))
                .thenReturn(true);
        RequestLatestData requestLatestData=RequestLatestData
                .builder()
                .staticDataType(StaticDataType.Inquiry)
                .build();
        RequestLatestData requestLatestData2=RequestLatestData
                .builder()
                .staticDataType(StaticDataType.Improvement)
                .build();
        List<ResponseLatestData> responseLatestData=adminDashBoardRepository.getLatestData(requestLatestData);
        List<ResponseLatestData> responseLatestData2=adminDashBoardRepository.getLatestData(requestLatestData2);
        responseLatestData.stream()
                .forEach(x->{
                    Assertions.assertThat(x.getCheckRead()).isTrue();
                });
        responseLatestData2.stream()
                .forEach(x->{
                    Assertions.assertThat(x.getCheckRead()).isTrue();
                });
    }

    @DisplayName("content detail 서비스로 접근시 호출이잘되는가")
    @Test
    void testContentReadDetail(){

        RequestDetail requestDetail=RequestDetail
                .builder()
                .alarmCheck("zczx")
                .contentId(board.getId())
                .staticDataType(StaticDataType.BOARD)
                .reportId(report.getId())
                .build();
        contentSearchService.getContentDetail(requestDetail);
        verify(redisService).adminReadCheckUpdate(any(String.class),any(StaticDataType.class),
                any(Long.class));
    }


    @DisplayName("content detail 서비스로 접근시 호출이안되는가")
    @Test
    void testContentReadDetail2(){

        RequestDetail requestDetail=RequestDetail
                .builder()
                .alarmCheck("zczx")
                .contentId(board.getId())
                .staticDataType(StaticDataType.BOARD)
                .build();
        contentSearchService.getContentDetail(requestDetail);
        RequestDetail requestDetail2=RequestDetail
                .builder()
                .contentId(board.getId())
                .reportId(report.getId())
                .staticDataType(StaticDataType.BOARD)
                .build();
        contentSearchService.getContentDetail(requestDetail2);
        RequestDetail requestDetail3=RequestDetail
                .builder()
                .contentId(board.getId())
                .staticDataType(StaticDataType.BOARD)
                .build();
        contentSearchService.getContentDetail(requestDetail3);


        verify(redisService,never()).adminReadCheckUpdate(any(String.class),any(StaticDataType.class),
                any(Long.class));
    }

    @DisplayName("detail 서비스로 접근시에 adminreadcheckupdate가 호출이되는가")
    @Test
    void testInquiryReadDetail(){
        RequestInquiryDetail requestInquiryDetail=RequestInquiryDetail
                .builder()
                .alarmCheck("dsfdsf")
                .id(inquiry.getId())
                .build();
        adminInquiryService.getInquiryDetail(requestInquiryDetail);
        verify(redisService).adminReadCheckUpdate(any(String.class),any(StaticDataType.class),
                any(Long.class));

    }
    @DisplayName("detail 서비스로 접근시에 adminreadcheckupdate가 호출이되는가")
    @Test
    void testImprovementReadDetail(){
        RequestImprovementDetail requestImprovementDetail=
                RequestImprovementDetail
                        .builder()
                        .alarmCheck("check")
                        .contentId(improvement.getId())
                        .build();
        adminImprovementService.getImproveDetail(requestImprovementDetail);
        verify(redisService).adminReadCheckUpdate(any(String.class),any(StaticDataType.class),
                any(Long.class));

    }
    @DisplayName("detail 서비스로 접근시에 adminreadcheckupdate가 호출이 안되는가")
    @Test
    void testNotReadDetail(){
        RequestImprovementDetail requestImprovementDetail= RequestImprovementDetail
                .builder()
                .contentId(improvement.getId())
                .build();
        adminImprovementService.getImproveDetail(requestImprovementDetail);
        RequestInquiryDetail requestInquiryDetail=RequestInquiryDetail
                .builder()
                .id(inquiry.getId())
                .build();
        adminInquiryService.getInquiryDetail(requestInquiryDetail);
        verify(redisService,never()).adminReadCheckUpdate(any(String.class),any(StaticDataType.class),
                any(Long.class));
    }
}
