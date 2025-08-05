package org.myteam.server.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.myteam.server.admin.utill.NeedDateTimeFix;

public record CommonResponseDto() {
    @Getter
    public static class AdminMemoResponse extends NeedDateTimeFix {
        private String writerName;
        private String content;
        public AdminMemoResponse(String writerName, String createDate, String content) {
            super(createDate);
            this.writerName = writerName;
            this.content = content;
        }
    }
}
