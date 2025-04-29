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
import org.myteam.server.report.dto.request.ReportRequest.*;
import org.myteam.server.report.dto.response.ReportResponse.*;
import org.myteam.server.report.util.ReportedContentValidator;
import org.myteam.server.report.util.ReportedContentValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
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
class ReportCreateFailureTest extends IntegrationTestSupport {

    @Autowired
    private ReportService reportService;
    @MockBean
    private ReportedContentValidatorFactory reportedContentValidatorFactory;
    private Member reporter;
    private Member reported;
    private Board board;
    private ReportedContentValidator validator;

    @BeforeEach
    void setUp() {
        reporter = createMember(1);
        reported = createMember(2);
        board = createBoard(reported, Category.BASEBALL, CategoryType.FREE, "title", "content");
        validator = mock(ReportedContentValidator.class);

        when(securityReadService.getMember()).thenReturn(reporter);
    }

    @Test
    @DisplayName("5분 내 3회 초과 시 신고 실패")
    void reportContent_limitExceeded() {
        when(redisService.isAllowed(any(), any())).thenReturn(false);
        when(redisService.getTimeToLive(any(), any())).thenReturn(100L);

        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.BOARD, board.getId(), BanReason.ETC
        );

        assertThrows(PlayHiveException.class, () ->
                reportService.reportContent(request, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("자기 자신을 신고하면 실패")
    void reportContent_selfReport() {

        ReportSaveRequest request = new ReportSaveRequest(
                reporter.getPublicId(), ReportType.BOARD, board.getId(), BanReason.ETC
        );

        assertThrows(PlayHiveException.class, () ->
                reportService.reportContent(request, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("신고 타입 매칭 실패")
    void reportContent_invalidReportType() {
        when(reportedContentValidatorFactory.getValidator(any())).thenReturn(null);

        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.BOARD, board.getId(), BanReason.ETC
        );

        assertThrows(PlayHiveException.class, () ->
                reportService.reportContent(request, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("타입이 잘못된 신고 생성 - 실패")
    void reportContent_success() {
        // given
        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.NONE, 100L, BanReason.HARASSMENT
        );

        // when && then
        assertThrows(PlayHiveException.class, () ->
                reportService.reportContent(request, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("신고 콘텐츠 존재하지 않으면 실패")
    void reportContent_invalidContent() {
        when(reportedContentValidatorFactory.getValidator(any())).thenReturn(validator);
        when(validator.isValid(any())).thenReturn(false);

        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.BOARD, board.getId(), BanReason.ETC
        );

        assertThrows(PlayHiveException.class, () ->
                reportService.reportContent(request, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("신고자와 콘텐츠 소유자가 일치하지 않으면 실패")
    void reportContent_invalidContentOwner() {
        when(reportedContentValidatorFactory.getValidator(any())).thenReturn(validator);
        when(validator.isValid(any())).thenReturn(true);
        when(validator.getOwnerPublicId(any())).thenReturn(UUID.randomUUID());

        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.BOARD, board.getId(), BanReason.ETC
        );

        assertThrows(PlayHiveException.class, () ->
                reportService.reportContent(request, "127.0.0.1")
        );
    }
}
