package org.myteam.server.report.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.admin.entity.AdminContentChangeLog;
import org.myteam.server.admin.utill.AdminControlType;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.global.domain.Category;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.ReportType;
import org.myteam.server.report.dto.request.ReportRequest.*;
import org.myteam.server.report.dto.response.ReportResponse.*;
import org.myteam.server.report.util.ReportedContentValidator;
import org.myteam.server.report.util.ReportedContentValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.myteam.server.admin.entity.QAdminContentChangeLog.adminContentChangeLog;

@Transactional
class ReportCreateSuccessTest extends IntegrationTestSupport {

    @Autowired
    private ReportService reportService;
    @MockBean
    MemberReadService mockMemberReadService;
    @MockBean
    private ReportedContentValidatorFactory reportedContentValidatorFactory;
    private Member reporter;
    private Member reported;
    private Board board;
    private ReportedContentValidator validator;

    @Autowired
    JPAQueryFactory queryFactory;

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
        when(mockMemberReadService.getAdminBot()).thenReturn(reporter);
        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.BOARD, board.getId(), BanReason.HARASSMENT,null
        );

        // when
        ReportSaveResponse response = reportService.reportContent(request, "127.0.0.1");

        // then
        assertThat(response).isNotNull();
    }
    @Test
    @DisplayName("신고 생성시 자동 숨김 테스트")
    void reportAutoHideTest(){
        ReportSaveRequest request = new ReportSaveRequest(
                reported.getPublicId(), ReportType.BOARD, board.getId(), BanReason.HARASSMENT,null
        );
        when(mockMemberReadService.getAdminBot()).thenReturn(reporter);
        when(validator.isValid(any())).thenReturn(true);
        when(validator.getOwnerPublicId(any())).thenReturn(reported.getPublicId());
        ReportSaveResponse response = reportService.reportContent(request, "127.0.0.1");

        Board board1=boardRepository.findById(board.getId()).get();
        assertThat(board1.getAdminControlType()).isEqualTo(AdminControlType.HIDDEN);

        AdminContentChangeLog adminContentChangeLog1=queryFactory.selectFrom(adminContentChangeLog)
                .where(adminContentChangeLog.contentId.eq(board1.getId()),
                        adminContentChangeLog.staticDataType.eq(StaticDataType.BOARD))
                .fetchOne();
        assertThat(adminContentChangeLog1.getAdminControlType()).isEqualTo(board1.getAdminControlType());
        assertThat(adminContentChangeLog1.getAdminControlType()).isEqualTo(AdminControlType.HIDDEN);
    }
}
