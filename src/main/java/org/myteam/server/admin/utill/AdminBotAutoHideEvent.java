package org.myteam.server.admin.utill;

import lombok.Builder;
import lombok.Getter;
import org.myteam.server.report.domain.ReportType;

@Getter
public class AdminBotAutoHideEvent {
    private Long contentId;
    private ReportType reportType;

    @Builder
    public AdminBotAutoHideEvent(Long contentId, ReportType reportType){
        this.contentId=contentId;
        this.reportType=reportType;
    }
}
