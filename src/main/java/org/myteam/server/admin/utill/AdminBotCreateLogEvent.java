package org.myteam.server.admin.utill;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.report.domain.ReportType;

@NoArgsConstructor
@Getter
public class AdminBotCreateLogEvent {

    private Long contentId;
    private StaticDataType staticDataType;
    private Boolean isMember;

    @Builder
    public AdminBotCreateLogEvent(Long contentId,StaticDataType staticDataType,Boolean isMember){
        this.contentId=contentId;
        this.staticDataType=staticDataType;
        this.isMember=isMember;
    }
}
