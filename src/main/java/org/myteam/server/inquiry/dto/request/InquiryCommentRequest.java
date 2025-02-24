package org.myteam.server.inquiry.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquiryCommentRequest {
    @NotNull(message = "댓글 내용은 필수 입니다.")
    private String comment;

    @Pattern(regexp = ".*\\.(png|jpeg|gif|webp|heic)$", message = "이미지는 png,jpeg,gif,webp,heic 확장자만 가능 합니다.")
    private String imageUrl;
}
