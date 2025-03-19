package org.myteam.server.improvement.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.comment.service.CommentReadService;
import org.myteam.server.improvement.service.ImprovementReadService;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.report.util.ReportedContentValidator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImprovementReportedContentValidator implements ReportedContentValidator {

    private final ImprovementReadService improvementReadService;

    @Override
    public boolean isValid(Long reportedContentId) {
        return improvementReadService.existsById(reportedContentId);
    }

    @Override
    public UUID getOwnerPublicId(Long reportedContentId) {
        return improvementReadService.findById(reportedContentId).getMember().getPublicId();
    }

    @Override
    public DomainType getSupportedDomain() {
        return DomainType.IMPROVEMENT;
    }
}
