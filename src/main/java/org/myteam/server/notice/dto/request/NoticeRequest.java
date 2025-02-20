package org.myteam.server.notice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public record NoticeRequest() {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public final class NoticeSaveResquest {
        @NotBlank(message = "제목을 입력해주세요")
        private String title;
        @NotBlank(message = "내용을 입력해주세요")
        private String content;
        private String imgUrl; // 썸네일 이미지
    }
}
