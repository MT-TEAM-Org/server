package org.myteam.server.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.utill.AdminControlType;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.improvement.domain.ImportantStatus;
import org.myteam.server.improvement.domain.ImprovementStatus;

public record AdminMemoRequestDto() {

    @Getter
    @NoArgsConstructor
    @Schema(description = "개선 요청 메모 작성 요청시 쓰이는 값입니다.")
    public static class AdminMemoImprovementRequest {
        @NotNull
        private Long contentId;
        @NotNull
        @Schema(description = "수정이 없다면 기존값을 수정했다면 수정값을 주세요", example = "PENDING RECEIVED COMPLETED")
        private ImprovementStatus improvementStatus;
        @NotNull
        @Schema(description = "수정이 없다면 기존값을 수정했다면 수정값을 주세요", example = "LOW NORMAL HIGH")
        private ImportantStatus importantStatus;
        @Schema(description = "내용이 없다면 null로 주세요")
        private String content;

        @Builder
        public AdminMemoImprovementRequest(Long contentId, String content
                , ImprovementStatus improvementStatus, ImportantStatus importantStatus) {
            this.contentId = contentId;
            this.importantStatus = importantStatus;
            this.improvementStatus = improvementStatus;
            this.content = content;
        }
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "문의 메모 작성 요청시 쓰이는 값입니다.")
    public static class AdminMemoInquiryRequest {
        @NotNull
        private Long contentId;
        @Schema(description = "내용이 없다면 null로 주세요")
        private String content;
        @NotNull
        @Schema(description = "문의 작성자의 이메일 입니다.")
        private String email;

        @Builder
        public AdminMemoInquiryRequest(Long contentId
                , String content, String email) {
            this.contentId = contentId;
            this.content = content;
            this.email = email;
        }
    }

    @Getter
    @NoArgsConstructor
    @Schema(description = "댓글 게시글 메모 작성 요청시 쓰이는 값입니다.")
    public static class AdminMemoContentRequest {
        @NotNull
        private Long contentId;
        @NotNull
        @Schema(description = "댓글이면 COMMENT 게시글이면 BOARD")
        private StaticDataType staticDataType;
        @NotNull
        @Schema(description = "수정이 없다면 기존값을 수정했다면 수정값을 주세요", example = "SHOW,PENDING,HIDDEN")
        private AdminControlType adminControlType;
        @Schema(description = "내용이 없다면 null로 주세요")
        private String content;

        @Builder
        public AdminMemoContentRequest(Long contentId, StaticDataType staticDataType, String content
                , AdminControlType adminControlType) {
            this.contentId = contentId;
            this.staticDataType = staticDataType;
            this.adminControlType = adminControlType;
            this.content = content;
        }
    }
}
