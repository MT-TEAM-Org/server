package org.myteam.server.notice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public record NoticeCommentRequest() {

    @Data
    @Builder
    @Getter
    @AllArgsConstructor
    public static final class NoticeCommentSaveRequest {
        @NotNull(message = "댓글 내용은 필수 입니다.")
        private String comment;

        @Pattern(regexp = ".*\\.(png|jpeg|gif|webp|heic)$", message = "이미지는 png,jpeg,gif,webp,heic 확장자만 가능 합니다.")
        private String imageUrl;
    }

    @Data
    @Builder
    @Getter
    @AllArgsConstructor
    public static final class NoticeCommentUpdateRequest {
        @NotNull(message = "댓글 내용은 필수 입니다.")
        private String comment;

        @Pattern(regexp = ".*\\.(png|jpeg|gif|webp|heic)$", message = "이미지는 png,jpeg,gif,webp,heic 확장자만 가능 합니다.")
        private String imageUrl;
    }
}
