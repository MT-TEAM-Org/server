package org.myteam.server.news.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.news.news.service.NewsReadService;
import org.myteam.server.news.newsComment.service.NewsCommentReadService;
import org.myteam.server.news.newsReply.service.NewsReplyReadService;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.report.util.ReportedContentValidator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsReportedContentValidator implements ReportedContentValidator {

    private final NewsReadService newsReadService;
    private final NewsCommentReadService newsCommentReadService;
    private final NewsReplyReadService newsReplyReadService;

    @Override
    public boolean isValid(Long reportedContentId) {
        return newsReadService.existsById(reportedContentId) ||
                newsCommentReadService.existsById(reportedContentId) ||
                newsReplyReadService.existsById(reportedContentId);
    }

    @Override
    public DomainType getSupportedDomain() {
        return DomainType.NEWS;
    }
}
