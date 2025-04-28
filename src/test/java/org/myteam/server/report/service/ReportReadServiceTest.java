package org.myteam.server.report.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.chat.domain.BanReason;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.Report;
import org.myteam.server.report.domain.ReportType;
import org.myteam.server.report.dto.response.ReportResponse.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.myteam.server.global.exception.ErrorCode.BOARD_NOT_FOUND;
import static org.myteam.server.global.exception.ErrorCode.INVALID_REFRESH_TOKEN;

@Transactional
class ReportReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private ReportReadService reportReadService;

    private Member reporter;
    private Member reported;
    private UUID reporterPublicId;
    private UUID reportedPublicId;
    private Report report;

    @Transactional
    @BeforeEach
    void setUp() {
        reporter = createMember(1);
        reported = createMember(2);
        reporterPublicId = reporter.getPublicId();
        reportedPublicId = reported.getPublicId();
        Board board = createBoard(reported, Category.ESPORTS, CategoryType.FREE, "title", "content");
        report = createReport(reporter, reported, BanReason.HARASSMENT, ReportType.BOARD, board.getId());
    }

    @Test
    @DisplayName("1. 전체 신고 리스트 조회 성공")
    void getReports_success() {
        // when
        Page<ReportSaveResponse> response = reportReadService.getReports(PageRequest.of(0, 10));

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("2. 특정 신고 조회 성공")
    void getReportById_success() {
        // when
        ReportSaveResponse response = reportReadService.getReportById(report.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getReportId()).isEqualTo(report.getId());
    }

    @Test
    @DisplayName("3. 특정 신고 조회 실패 - 존재하지 않는 ID")
    void getReportById_notFound() {
        // when & then
        assertThatThrownBy(() -> reportReadService.getReportById(9999L))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.REPORT_NOT_FOUND.getMsg());
    }

    @Test
    @DisplayName("4. 내가 받은 신고 내역 조회 성공")
    void getReceivedReports_success() {
        // when
        Page<ReportSaveResponse> response = reportReadService.getReceivedReports(reportedPublicId, PageRequest.of(0, 10));

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("5. 내가 보낸 신고 내역 조회 성공")
    void getSentReports_success() {
        // when
        Page<ReportSaveResponse> response = reportReadService.getSentReports(reporterPublicId, PageRequest.of(0, 10));

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isNotEmpty();
    }
}