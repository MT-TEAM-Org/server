package org.myteam.server.admin.service;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.admin.entity.AdminInquiryChangeLog;
import org.myteam.server.admin.utill.*;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.improvement.domain.ImportantStatus;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.report.domain.ReportType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


import java.util.Optional;

import static org.myteam.server.admin.dto.request.AdminMemoRequestDto.*;
import static org.myteam.server.board.domain.QBoard.board;
import static org.myteam.server.board.domain.QBoardCount.boardCount;
import static org.myteam.server.comment.domain.QComment.comment1;
import static org.myteam.server.report.domain.QReport.report;

@RequiredArgsConstructor
@Component
public class AdminBotListener {
    private final JPAQueryFactory queryFactory;
    private final CreateAdminMemo createAdminMemo;
    @EventListener
    public void reportAutoHide(AdminBotAutoHideEvent autoHideEvent){
        Long reportCount= Optional.ofNullable(queryFactory.select(report.count())
                        .from(report)
                        .where(report.reportedContentId.eq(autoHideEvent.getContentId())
                                ,report.reportType.eq(autoHideEvent.getReportType()))
                        .fetchOne())
                .orElse(0L);
        if(autoHideEvent.getReportType().equals(ReportType.BOARD)){
            int viewCount=queryFactory.select(board.boardCount.viewCount)
                    .from(board)
                    .join(boardCount)
                    .on(boardCount.board.eq(board))
                    .where(board.id.eq(autoHideEvent.getContentId()))
                    .fetchOne();
            if(reportCount>=viewCount*0.8&&viewCount>=10){
                AdminMemoContentRequest adminMemoContentRequest=
                        AdminMemoContentRequest
                                .builder()
                                .contentId(autoHideEvent.getContentId())
                                .adminControlType(AdminControlType.HIDDEN)
                                .content("신고 비율이 초과되어서 자동 숨김처리 되었습니다.")
                                .staticDataType(StaticDataType.BOARD)
                                .auto("auto")
                                .build();
                createAdminMemo.createContentAdminMemo(adminMemoContentRequest,queryFactory);
            }
            return;
        }
        if(autoHideEvent.getReportType().equals(ReportType.COMMENT)){
            Comment comment=queryFactory.selectFrom(comment1)
                    .where(comment1.id.eq(autoHideEvent.getContentId()))
                    .fetchOne();
            int recommendCount=comment.getRecommendCount();
            if(reportCount>=recommendCount*0.8&&recommendCount>=3){
                AdminMemoContentRequest adminMemoContentRequest=
                        AdminMemoContentRequest
                                .builder()
                                .contentId(autoHideEvent.getContentId())
                                .adminControlType(AdminControlType.HIDDEN)
                                .content("신고 비율이 초과되어서 자동 숨김처리 되었습니다.")
                                .staticDataType(StaticDataType.COMMENT)
                                .auto("auto")
                                .build();
                createAdminMemo.createContentAdminMemo(adminMemoContentRequest,queryFactory);
            }
            return;
        }
    }
    @EventListener
    public void adminBotAutoLogEvent(AdminBotCreateLogEvent adminBotEvent){
        if(adminBotEvent.getStaticDataType().equals(StaticDataType.Improvement)){
            AdminMemoImprovementRequest adminMemoImprovementRequest=AdminMemoImprovementRequest
                    .builder()
                    .importantStatus(ImportantStatus.NORMAL)
                    .improvementStatus(ImprovementStatus.PENDING)
                    .contentId(adminBotEvent.getContentId())
                    .auto("auto")
                    .build();
            createAdminMemo.createImprovementMemo(adminMemoImprovementRequest,queryFactory);
            return;
        }
        AdminMemoInquiryRequest adminMemoInquiryRequest=AdminMemoInquiryRequest
                .builder()
                .contentId(adminBotEvent.getContentId())
                .isMember(adminBotEvent.getIsMember())
                .build();
        createAdminMemo.adminBotCreateInquiryLog(adminMemoInquiryRequest);
    }
}
