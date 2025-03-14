package org.myteam.server.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.myteam.server.comment.domain.CommentType;

import java.util.UUID;

public record CommentRequest() {

    @Data
    @Builder
    @Getter
    @AllArgsConstructor
    public static final class CommentSaveRequest {

        @Schema(description = "댓글 유형", example = "NOTICE", allowableValues = {"NOTICE", "BOARD", "NEWS", "IMPROVEMENT", "INQUIRY"})
        @NotNull(message = "댓글 유형(type)은 필수 입력 값입니다.")
        private CommentType type;

        @NotNull(message = "댓글 내용은 필수 입니다.")
        private String comment;

        @Pattern(regexp = ".*\\.(png|jpeg|gif|webp|heic)$", message = "이미지는 png,jpeg,gif,webp,heic 확장자만 가능 합니다.")
        private String imageUrl;
        private Long parentId;

        private UUID mentionedPublicId;
    }

    @Data
    @Builder
    @Getter
    @AllArgsConstructor
    public static final class CommentDeleteRequest {
        @Schema(description = "댓글 유형", example = "NOTICE", allowableValues = {"NOTICE", "BOARD", "NEWS", "IMPROVEMENT", "INQUIRY"})
        @NotNull(message = "댓글 유형(type)은 필수 입력 값입니다.")
        private CommentType type;

        @NotNull
        private Long commentId;
    }
}
