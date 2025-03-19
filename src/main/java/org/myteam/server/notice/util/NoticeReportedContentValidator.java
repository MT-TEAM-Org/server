package org.myteam.server.notice.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.notice.service.NoticeReadService;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.report.util.ReportedContentValidator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NoticeReportedContentValidator implements ReportedContentValidator {

    private final NoticeReadService noticeReadService;

    @Override
    public boolean isValid(Long reportedContentId) {
        return noticeReadService.existsById(reportedContentId);
    }

    @Override
    public UUID getOwnerPublicId(Long reportedContentId) {
        return noticeReadService.findById(reportedContentId).getMember().getPublicId();
    }

    @Override
    public DomainType getSupportedDomain() {
        return DomainType.NOTICE;
    }
}
