package org.myteam.server.inquiry.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.UUID;

public record InquiryCommentRequest() {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class InquiryCommentSaveRequest {
        @NotNull(message = "댓글 내용은 필수 입니다.")
        private String comment;

        @Pattern(regexp = ".*\\.(png|jpeg|gif|webp|heic)$", message = "이미지는 png,jpeg,gif,webp,heic 확장자만 가능 합니다.")
        private String imageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class InquiryCommentUpdateRequest {
        @NotNull(message = "댓글 내용은 필수 입니다.")
        private String comment;

        @Pattern(regexp = ".*\\.(png|jpeg|gif|webp|heic)$", message = "이미지는 png,jpeg,gif,webp,heic 확장자만 가능 합니다.")
        private String imageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class InquiryReplySaveRequest {
        @NotNull(message = "대댓글 내용은 필수 입니다.")
        private String comment;

        @Pattern(regexp = ".*\\.(png|jpeg|gif|webp|heic)$", message = "이미지는 png,jpeg,gif,webp,heic 확장자만 가능 합니다.")
        private String imageUrl;

        private UUID mentionedPublicId;
    }


}
