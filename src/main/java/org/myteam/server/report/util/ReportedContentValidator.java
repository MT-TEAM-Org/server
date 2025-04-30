package org.myteam.server.report.util;

import org.myteam.server.report.domain.DomainType;

import java.util.UUID;

public interface ReportedContentValidator {
    boolean isValid(Long reportedContentId);
    UUID getOwnerPublicId(Long reportedContentId);
    DomainType getSupportedDomain();
}
