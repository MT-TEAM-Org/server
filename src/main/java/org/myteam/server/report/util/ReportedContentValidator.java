package org.myteam.server.report.util;

import org.myteam.server.report.domain.DomainType;

public interface ReportedContentValidator {
    boolean isValid(Long reportedContentId);
    DomainType getSupportedDomain();
}
