package org.myteam.server.admin.dto.ctes;


import com.blazebit.persistence.CTE;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import org.myteam.server.report.domain.ReportType;

@CTE
@Entity
public class SimpleReportCte {

    @Id
    private Long reportedId;
    @Enumerated(EnumType.STRING)
    private ReportType reportType;
}
