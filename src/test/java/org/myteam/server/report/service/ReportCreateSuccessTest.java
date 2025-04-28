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
class ReportCreateSuccessTest extends IntegrationTestSupport {

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
        when(redisService.isAllowed(any(), any())).thenReturn(true);
        when(reportedContentValidatorFactory.getValidator(any())).thenReturn(validator);
    }

    @Test
    @DisplayName("신고 정상 생성 성공")
    void reportContent_success() {
        // given
        when(validator.isValid(any())).thenReturn(true);
        when(validator.getOwnerPublicId(any())).thenReturn(reported.getPublicId());

        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.BOARD, board.getId(), BanReason.HARASSMENT
        );

        // when
        ReportSaveResponse response = reportService.reportContent(request, "127.0.0.1");

        // then
        assertThat(response).isNotNull();
    }
}
