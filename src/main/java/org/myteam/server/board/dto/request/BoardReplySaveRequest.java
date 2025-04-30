package org.myteam.server.board.dto.request;

import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardReplySaveRequest {
    private String comment;

    @Pattern(regexp = ".*\\.(png|jpeg|gif|webp|heic)$", message = "이미지는 png,jpeg,gif,webp,heic 확장자만 가능 합니다.")
    private String imageUrl;

    private UUID mentionedPublicId;
}