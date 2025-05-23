package org.myteam.server.news.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.service.NewsReadService;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.report.util.ReportedContentValidator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NewsReportedContentValidator implements ReportedContentValidator {

    private final NewsReadService newsReadService;

    @Override
    public boolean isValid(Long reportedContentId) {
        return newsReadService.existsById(reportedContentId);
    }

    @Override
    public UUID getOwnerPublicId(Long reportedContentId) {
        return null;
    }

    @Override
    public DomainType getSupportedDomain() {
        return DomainType.NEWS;
    }
}
