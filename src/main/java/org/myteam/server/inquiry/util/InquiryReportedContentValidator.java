package org.myteam.server.inquiry.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.service.BoardCommentReadService;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.board.service.BoardReplyReadService;
import org.myteam.server.inquiry.service.InquiryCommentReadService;
import org.myteam.server.inquiry.service.InquiryReadService;
import org.myteam.server.inquiry.service.InquiryReplyReadService;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.report.util.ReportedContentValidator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InquiryReportedContentValidator implements ReportedContentValidator {

    private final InquiryReadService inquiryReadService;
    private final InquiryCommentReadService inquiryCommentReadService;
    private final InquiryReplyReadService inquiryReplyReadService;

    @Override
    public boolean isValid(Long reportedContentId) {
        return inquiryReadService.existsById(reportedContentId) ||
                inquiryCommentReadService.existsById(reportedContentId) ||
                inquiryReplyReadService.existsById(reportedContentId);
    }

    @Override
    public DomainType getSupportedDomain() {
        return DomainType.INQUIRY;
    }
}
