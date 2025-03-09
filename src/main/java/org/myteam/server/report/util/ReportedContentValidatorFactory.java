package org.myteam.server.report.util;

import org.myteam.server.report.domain.DomainType;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReportedContentValidatorFactory {

    private final Map<DomainType, ReportedContentValidator> validatorMap;

    public ReportedContentValidatorFactory(List<ReportedContentValidator> validators) {
        this.validatorMap = validators.stream()
                .collect(Collectors.toMap(ReportedContentValidator::getSupportedDomain, validator -> validator));
    }

    public ReportedContentValidator getValidator(DomainType domainType) {
        return validatorMap.get(domainType);
    }
}
