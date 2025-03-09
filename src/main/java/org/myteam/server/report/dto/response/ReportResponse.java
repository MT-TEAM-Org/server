package org.myteam.server.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myteam.server.chat.domain.BanReason;
import org.myteam.server.report.domain.Report;
import org.myteam.server.report.domain.ReportReason;
import org.myteam.server.report.domain.ReportType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record ReportResponse() {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ReportSaveResponse {
        private Long reportId; // 생성된 신고의 ID
        private UUID reporterPublicId; // 신고자의 publicId
        private UUID reportedPublicId; // 신고 대상자의 publicId
        private ReportType reportType; // 신고 유형 (예: COMMENT, POST, USER)
        private Long reportedContentId; // 신고된 컨텐츠 ID (예: 댓글 ID, 게시글 ID)
        private BanReason reason; // 신고 사유 목록
        private LocalDateTime createdAt; // 신고 생성 시간
        private String createdIp; // 신고 요청자의 IP 주소

        public static ReportSaveResponse createResponse(Report report) {
            return ReportSaveResponse.builder()
                    .reportId(report.getId())
                    .reporterPublicId(report.getReporter().getPublicId())
                    .reportedPublicId(report.getReported().getPublicId())
                    .reportType(report.getReportType())
                    .reportedContentId(report.getReportedContentId())
                    .reason(report.getReason())
                    .createdAt(report.getCreateDate())
                    .createdIp(report.getReportIp())
                    .build();
        }
    }
}
