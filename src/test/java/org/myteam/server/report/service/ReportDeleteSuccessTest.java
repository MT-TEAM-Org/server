package org.myteam.server.report.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.chat.domain.BanReason;
import org.myteam.server.global.domain.Category;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.Report;
import org.myteam.server.report.domain.ReportType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Transactional
class ReportDeleteSuccessTest extends IntegrationTestSupport {

    @Autowired
    private ReportService reportService;

    private Report report;

    @BeforeEach
    void setUp() {
        Member reporter = createMember(1);
        Member reported = createMember(2);
        Board board = createBoard(reported, Category.BASEBALL, CategoryType.FREE, "title", "content");

        report = reportRepository.save(
                Report.createReport(reporter, reported, "127.0.0.1", ReportType.BOARD, board.getId(), BanReason.ETC)
        );
    }

    @Test
    @DisplayName("신고 정상 삭제 성공")
    void deleteReport_success() {
        assertDoesNotThrow(() ->
                reportService.deleteReport(report.getId())
        );
    }
}
