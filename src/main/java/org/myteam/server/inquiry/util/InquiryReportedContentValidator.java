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
//    private final InquiryCommentReadService inquiryCommentReadService;
//    private final InquiryReplyReadService inquiryReplyReadService;

    @Override
    public boolean isValid(Long reportedContentId) {
        return inquiryReadService.existsById(reportedContentId);
//                inquiryCommentReadService.existsById(reportedContentId) ||
//                inquiryReplyReadService.existsById(reportedContentId);
    }

    @Override
    public UUID getOwnerPublicId(Long reportedContentId) {
        if (inquiryReadService.existsById(reportedContentId)) {
            return inquiryReadService.findInquiryById(reportedContentId).getMember().getPublicId();
        } else {
            return null;
        }
//        else if (inquiryCommentReadService.existsById(reportedContentId)) {
//            return inquiryCommentReadService.findById(reportedContentId).getMember().getPublicId();
//        } else {
//            return inquiryReplyReadService.findById(reportedContentId).getMember().getPublicId();
//        }
    }

    @Override
    public DomainType getSupportedDomain() {
        return DomainType.INQUIRY;
    }
}
