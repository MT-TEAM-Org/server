package org.myteam.server.report.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.report.domain.ReportType;

import java.util.UUID;

public record ReportRequest() {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ReportSaveRequest {
        @NotNull(message = "신고 대상자의 Public ID는 필수 입력 값입니다.")
        private UUID reportedPublicId;

        @NotNull(message = "신고 유형(ReportType)은 필수 입력 값입니다.")
        private ReportType reportType;

        @NotNull(message = "신고된 컨텐츠 ID는 필수 입력 값입니다.")
        private Long reportedContentId;

        @NotNull(message = "신고 사유(BanReason)는 선택해야 합니다.")
        private BanReason reasons;

        private String reportDescription;

        public ReportSaveRequest(UUID reportedPublicId, ReportType reportType, Long reportedContentId, BanReason reasons) {
            this.reportedPublicId = reportedPublicId;
            this.reportType = reportType;
            this.reportedContentId = reportedContentId;
            this.reasons = reasons;
        }
    }
}
