package org.myteam.server.comment.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.comment.service.CommentReadService;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.report.util.ReportedContentValidator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommentReportedContentValidator implements ReportedContentValidator {

    private final CommentReadService commentReadService;

    @Override
    public boolean isValid(Long reportedContentId) {
        return commentReadService.existsById(reportedContentId);
    }

    @Override
    public UUID getOwnerPublicId(Long reportedContentId) {
        return commentReadService.findById(reportedContentId).getMember().getPublicId();
    }

    @Override
    public DomainType getSupportedDomain() {
        return DomainType.COMMENT;
    }
}
