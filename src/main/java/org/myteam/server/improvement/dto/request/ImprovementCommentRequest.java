package org.myteam.server.improvement.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

public record ImprovementCommentRequest() {

    @Data
    @Builder
    @Getter
    @AllArgsConstructor
    public static final class ImprovementCommentSaveRequest {
        @NotNull(message = "댓글 내용은 필수 입니다.")
        private String comment;

        @Pattern(regexp = ".*\\.(png|jpeg|gif|webp|heic)$", message = "이미지는 png,jpeg,gif,webp,heic 확장자만 가능 합니다.")
        private String imageUrl;
    }

    @Data
    @Builder
    @Getter
    @AllArgsConstructor
    public static final class ImprovementCommentUpdateRequest {
        @NotNull(message = "댓글 내용은 필수 입니다.")
        private String comment;

        @Pattern(regexp = ".*\\.(png|jpeg|gif|webp|heic)$", message = "이미지는 png,jpeg,gif,webp,heic 확장자만 가능 합니다.")
        private String imageUrl;
    }

    @Data
    @Builder
    @Getter
    @AllArgsConstructor
    public static final class ImprovementReplySaveRequest {
        @NotNull(message = "대댓글 내용은 필수 입니다.")
        private String comment;

        @Pattern(regexp = ".*\\.(png|jpeg|gif|webp|heic)$", message = "이미지는 png,jpeg,gif,webp,heic 확장자만 가능 합니다.")
        private String imageUrl;

        private UUID mentionedPublicId;
    }
}
