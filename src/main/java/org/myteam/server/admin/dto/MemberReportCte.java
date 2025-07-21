package org.myteam.server.admin.dto;


import com.blazebit.persistence.CTE;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import org.myteam.server.report.domain.ReportType;

@Entity
@Getter
@CTE
public class MemberReportCte {

    @Id
    private Long reportedId;
    @Enumerated(EnumType.STRING)
    private ReportType reportType;

}
