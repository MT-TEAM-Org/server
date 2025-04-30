package org.myteam.server.board.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.report.util.ReportedContentValidator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BoardReportedContentValidator implements ReportedContentValidator {

    private final BoardReadService boardReadService;

    @Override
    public boolean isValid(Long reportedContentId) {
        return boardReadService.existsById(reportedContentId);
    }

    @Override
    public UUID getOwnerPublicId(Long reportedContentId) {
        return boardReadService.findById(reportedContentId).getMember().getPublicId();
    }

    @Override
    public DomainType getSupportedDomain() {
        return DomainType.BOARD;
    }
}
