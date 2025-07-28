package org.myteam.server.admin.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.improvement.domain.ImportantStatus;
import org.myteam.server.improvement.domain.ImprovementStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ImproveRequestDto() {


    @Getter
    @NoArgsConstructor
    public final static class RequestImprovementList {

        @Schema(example = "PENDING RECEIVED COMPLETED")
        private ImprovementStatus improvementStatus;
        private String title;
        private String content;
        private String nickName;
        private String email;
        @Schema(example = "2025.06.06")
        private String startTime;
        @Schema(example = "2025.06.06")
        private String endTime;
        @NotNull(message = "offset은 비면 안됩니다.")
        private int offset;
        @Schema(example = "LOW NORMAL HIGH")
        private ImportantStatus importantStatus;

        @Builder
        public RequestImprovementList(ImprovementStatus improvementStatus,
                                      String title, String content, String nickName,
                                      String startTime, String endTime, String email, int offset
                , ImportantStatus importantStatus) {
            this.improvementStatus = improvementStatus;
            this.title = title;
            this.content = content;
            this.nickName = nickName;
            this.startTime = startTime;
            this.endTime = endTime;
            this.email = email;
            this.offset = offset;
            this.importantStatus = importantStatus;
        }

        public Integer getOffset() {
            return this.offset - 1;
        }

        public LocalDateTime provideStartTime() {
            if (startTime == null) {
                return null;
            }
            LocalDate localDate = LocalDate.parse(startTime, DateFormatUtil.formatByDot);
            return localDate.atStartOfDay();
        }

        public LocalDateTime provideEndTime() {
            if (endTime == null) {
                return null;
            }
            LocalDate localDate = LocalDate.parse(endTime, DateFormatUtil.formatByDot);
            return localDate.atStartOfDay();
        }
    }


    @Getter
    @NoArgsConstructor
    public final static class RequestImprovementDetail {
        @NotNull(message = "contentid는 비면안됩니다.")
        @Schema(description = "필수값 입니다.")
        private Long contentId;

        @Builder
        public RequestImprovementDetail(Long contentId) {
            this.contentId = contentId;
        }
    }


    @Getter
    @NoArgsConstructor
    public final static class RequestMemberImproveList {
        @NotNull(message = "publicid는 필수입니다.")
        @Schema(description = "회원식별 아이디값.필수값 입니다.")
        private UUID publicId;
        @NotNull(message = "offset은 필수입니다.")
        @Schema(description = "필수값 입니다.")
        private int offset;

        @Builder
        public RequestMemberImproveList(UUID publicId, int offset) {
            this.publicId = publicId;
            this.offset = offset;
        }

        public Integer getOffSet() {
            return this.offset - 1;
        }
    }


}
