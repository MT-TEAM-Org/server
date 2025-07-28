package org.myteam.server.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

public record CommonResponseDto() {
    @Getter
    @AllArgsConstructor
    public static class AdminMemoResponse{
        private String writerName;
        @Schema(example = "2025.06.06")
        private String createDate;
        private String content;
        public void updateCreateDate(String date) {
            this.createDate = date;
        }
    }
}
