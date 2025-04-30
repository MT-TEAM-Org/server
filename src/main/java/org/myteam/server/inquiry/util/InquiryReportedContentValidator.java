package org.myteam.server.inquiry.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.inquiry.domain.Inquiry;
//import org.myteam.server.inquiry.service.InquiryCommentReadService;
import org.myteam.server.inquiry.service.InquiryReadService;
//import org.myteam.server.inquiry.service.InquiryReplyReadService;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.report.util.ReportedContentValidator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InquiryReportedContentValidator implements ReportedContentValidator {

    private final InquiryReadService inquiryReadService;

    @Override
    public boolean isValid(Long reportedContentId) {
        return inquiryReadService.existsById(reportedContentId);
    }

    @Override
    public UUID getOwnerPublicId(Long reportedContentId) {
        return inquiryReadService.findInquiryById(reportedContentId).getMember().getPublicId();
    }

    @Override
    public DomainType getSupportedDomain() {
        return DomainType.INQUIRY;
    }
}
