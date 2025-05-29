package org.myteam.server.report.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.report.domain.Report;
import org.myteam.server.report.domain.ReportType;
import org.myteam.server.report.dto.request.ReportRequest.*;
import org.myteam.server.report.dto.response.ReportResponse.*;
import org.myteam.server.report.util.ReportedContentValidator;
import org.myteam.server.report.util.ReportedContentValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
class ReportServiceTest extends IntegrationTestSupport {

    @Autowired
    private ReportService reportService;
    @MockBean
    private ReportedContentValidatorFactory reportedContentValidatorFactory;

    private Member reporter;
    private Member reported;
    private Member admin;
    private UUID reporterPublicId;
    private UUID reportedPublicId;
    private Board board;
    private News news;
    private Improvement improvement;
    private Notice notice;
    private Inquiry inquiry;
    private Comment comment;
    private ReportedContentValidator reportedContentValidator;

    @BeforeEach
    void setUp() {
        reporter = createMember(1);
        reported = createMember(2);
        admin = createAdmin(3);
        reporterPublicId = reporter.getPublicId();
        reportedPublicId = reported.getPublicId();

        board = createBoard(reported, Category.BASEBALL, CategoryType.FREE, "title", "content");
        news = createNews(0, Category.BASEBALL, 0);
        improvement = createImprovement(reported, false);
        notice = createNotice(admin, "title", "content", null);
        inquiry = createInquiry(reported);

        reportedContentValidator = mock(ReportedContentValidator.class);

        when(securityReadService.getMember()).thenReturn(reporter);
        when(redisService.isAllowed(anyString(), anyString())).thenReturn(true);
        when(reportedContentValidatorFactory.getValidator(any())).thenReturn(reportedContentValidator);

        comment = createNewsComment(news, reported, "댓글");
    }

    @Test
    @DisplayName("1. 정상 신고 생성 성공")
    void reportContent_success_board() {
        // given
        when(reportedContentValidator.isValid(any())).thenReturn(true);
        when(reportedContentValidator.getOwnerPublicId(any())).thenReturn(reported.getPublicId());

        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.BOARD, board.getId(), BanReason.HARASSMENT
        );

        // when
        ReportSaveResponse response = reportService.reportContent(request, "127.0.0.1");

        // then
        assertThat(response).isNotNull();
    }
    @Test
    @DisplayName("1-1. 정상 신고 생성 성공")
    void reportContent_success_news() {
        // given
        when(reportedContentValidator.isValid(any())).thenReturn(true);
        when(reportedContentValidator.getOwnerPublicId(any())).thenReturn(reported.getPublicId());

        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.NEWS, news.getId(), BanReason.HARASSMENT
        );

        // when
        ReportSaveResponse response = reportService.reportContent(request, "127.0.0.1");

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("1-2. 정상 신고 생성 성공")
    void reportContent_success() {
        // given
        when(reportedContentValidator.isValid(any())).thenReturn(true);
        when(reportedContentValidator.getOwnerPublicId(any())).thenReturn(reported.getPublicId());

        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.INQUIRY, inquiry.getId(), BanReason.HARASSMENT
        );

        // when
        ReportSaveResponse response = reportService.reportContent(request, "127.0.0.1");

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("1-3. 정상 신고 생성 성공")
    void reportContent_success_imporvement() {
        // given
        when(reportedContentValidator.isValid(any())).thenReturn(true);
        when(reportedContentValidator.getOwnerPublicId(any())).thenReturn(reported.getPublicId());

        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.IMPROVEMENT, improvement.getId(), BanReason.HARASSMENT
        );

        // when
        ReportSaveResponse response = reportService.reportContent(request, "127.0.0.1");

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("1-4. 정상 신고 생성 성공")
    void reportContent_success_notice() {
        // given
        when(reportedContentValidator.isValid(any())).thenReturn(true);
        when(reportedContentValidator.getOwnerPublicId(any())).thenReturn(reported.getPublicId());

        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.NOTICE, notice.getId(), BanReason.HARASSMENT
        );

        // when
        ReportSaveResponse response = reportService.reportContent(request, "127.0.0.1");

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("1-5. 정상 신고 생성 성공")
    void reportContent_success_comment() {
        // given
        when(reportedContentValidator.isValid(any())).thenReturn(true);
        when(reportedContentValidator.getOwnerPublicId(any())).thenReturn(reported.getPublicId());

        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.COMMENT, comment.getId(), BanReason.HARASSMENT
        );

        // when
        ReportSaveResponse response = reportService.reportContent(request, "127.0.0.1");

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("2. 5분 내 3회 초과 신고 제한 - 예외 발생")
    void reportContent_limitExceeded() {
        // given
        when(redisService.isAllowed(anyString(), anyString())).thenReturn(false);
        when(redisService.getTimeToLive(anyString(), anyString())).thenReturn(100L);

        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.BOARD, board.getId(), BanReason.HARASSMENT
        );

        // when & then
        assertThrows(PlayHiveException.class, () ->
                reportService.reportContent(request, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("3. 자기 자신을 신고하는 경우 예외 발생")
    void reportContent_selfReport() {
        // given

        ReportSaveRequest request = new ReportSaveRequest(
                reporter.getPublicId(), ReportType.BOARD, board.getId(), BanReason.PROMOTIONAL_OR_ILLEGAL_ADS
        );

        // when & then
        assertThrows(PlayHiveException.class, () ->
                reportService.reportContent(request, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("4. 신고 타입 매칭 실패 시 예외 발생")
    void reportContent_invalidReportType() {
        // given
        when(reportedContentValidatorFactory.getValidator(any())).thenReturn(null);

        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.BOARD, board.getId(), BanReason.ETC
        );

        // when & then
        assertThrows(PlayHiveException.class, () ->
                reportService.reportContent(request, "127.0.0.1")
        );
    }

    @Test
    @DisplayName("5. 신고 대상 콘텐츠가 존재하지 않는 경우 예외 발생")
    void reportContent_invalidContent() {
        // given
        when(reportedContentValidator.isValid(any())).thenReturn(false);

        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.BOARD, board.getId(), BanReason.HARASSMENT
        );

        // when & then
        assertThrows(PlayHiveException.class, () ->
                reportService.reportContent(request, "127.0.0.1")
        );
    }

//    @Test
//    @DisplayName("6. 신고 대상자와 콘텐츠 소유자가 일치하지 않는 경우 예외 발생")
//    void reportContent_invalidContentOwner() {
//        // given
//        Board newBoard = board = createBoard(reporter, Category.BASEBALL, CategoryType.FREE, "title", "content");
//        when(reportedContentValidator.isValid(any())).thenReturn(true);
//        when(reportedContentValidator.getOwnerPublicId(any())).thenReturn(reportedPublicId); // 엉뚱한 소유자
//
//        ReportSaveRequest request = new ReportSaveRequest(
//                reported.getPublicId(), ReportType.BOARD, newBoard.getId(), BanReason.HARASSMENT
//        );
//
//        // when & then
//        assertThrows(PlayHiveException.class, () ->
//                reportService.reportContent(request, "127.0.0.1")
//        );
//    }

    @Test
    @DisplayName("7. 신고 삭제 성공")
    void deleteReport_success() {
        // given
        Report report = reportRepository.save(Report.createReport(
                reporter, reported, "127.0.0.1", ReportType.BOARD, board.getId(), BanReason.PROMOTIONAL_OR_ILLEGAL_ADS
        ));

        // when & then
        assertDoesNotThrow(() -> reportService.deleteReport(report.getId()));
    }

    @Test
    @DisplayName("8. 존재하지 않는 신고 삭제 시 예외 발생")
    void deleteReport_notFound() {
        // when & then
        assertThrows(PlayHiveException.class, () ->
                reportService.deleteReport(9999L)
        );
    }
}