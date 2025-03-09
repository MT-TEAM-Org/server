package org.myteam.server.board.util;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.Board;
import org.myteam.server.board.service.BoardCommentReadService;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.board.service.BoardReplyReadService;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.report.util.ReportedContentValidator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BoardReportedContentValidator implements ReportedContentValidator {

    private final BoardReadService boardReadService;
    private final BoardCommentReadService boardCommentReadService;
    private final BoardReplyReadService boardReplyReadService;

    @Override
    public boolean isValid(Long reportedContentId) {
        return boardReadService.existsById(reportedContentId) ||
                boardCommentReadService.existsById(reportedContentId) ||
                boardReplyReadService.existsById(reportedContentId);
    }

    @Override
    public UUID getOwnerPublicId(Long reportedContentId) {
        if (boardReadService.existsById(reportedContentId)) {
            return boardReadService.findById(reportedContentId).getMember().getPublicId();
        } else if (boardCommentReadService.existsById(reportedContentId)) {
            return boardCommentReadService.findById(reportedContentId).getMember().getPublicId();
        } else {
            return boardReplyReadService.findById(reportedContentId).getMember().getPublicId();
        }
    }

    @Override
    public DomainType getSupportedDomain() {
        return DomainType.BOARD;
    }
}
