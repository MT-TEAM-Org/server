package org.myteam.server.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.utill.AdminControlType;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.global.util.date.DateFormatUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContentRequestDto() {
    @Getter
    @NoArgsConstructor
    public static class RequestContentData {
        @Schema(description = "탐색시에 댓글 게시글 등 어떤값으로 탐색할지를 의미합니다.",
        examples= "TITLE,CONTENT,TITLE_CONTENT,NICKNAME, COMMENT")
        private BoardSearchType boardSearchType;
        private String searchKeyWord;
        @Schema(description = "비어있다면 복합값을 돌려주고 특정 타입선택시 해당 되는것만 보여줍니다.")
        private StaticDataType staticDataType;
        private Boolean isReported;
        @Schema(description = "관리자 컨트롤 타입입니다.-> 보여줄지 말지 여부라고 생각하면됩니다.", example = "SHOW,PENDING,HIDDEN")
        private AdminControlType adminControlType;
        @Schema(example = "2025.06.06")
        private String startTime;
        @Schema(example = "2025.06.06")
        private String endTime;
        @NotNull(message = "offset은 있어야합니다.")
        @Schema(description = "필수값 입니다.")
        private int offset;

        @Builder
        public RequestContentData(BoardSearchType boardSearchType, String searchKeyWord, StaticDataType staticDataType,
                                  Boolean reported, AdminControlType adminControlType, String startTime,
                                  String endTime, int offset) {
            this.boardSearchType = boardSearchType;
            this.searchKeyWord = searchKeyWord;
            this.staticDataType = staticDataType;
            this.isReported = reported;
            this.adminControlType = adminControlType;
            this.startTime = startTime;
            this.endTime = endTime;
            this.offset = offset;
        }

        public int getOffset() {
            return this.offset - 1;
        }

        public LocalDateTime provideStartTime() {
            if (this.startTime == null) {
                return null;
            }
            LocalDate localDate = LocalDate.parse(startTime, DateFormatUtil.formatByDot);
            return localDate.atStartOfDay();
        }

        public LocalDateTime provideEndTime() {
            if (this.endTime == null) {
                return null;
            }
            LocalDate localDate = LocalDate.parse(endTime, DateFormatUtil.formatByDot);
            return localDate.atStartOfDay();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class RequestDetail {
        @NotNull
        @Schema(description = "게시물이면 BOARD 댓글이면 COMMENT 입니다 필수값입니다.")
        private StaticDataType staticDataType;
        @NotNull(message ="contentid는 비면안됩니다.")
        @Schema(description = "필수값입니다.")
        private Long contentId;
        @Builder
        public RequestDetail(StaticDataType staticDataType, Long contentId) {
            this.staticDataType = staticDataType;
            this.contentId = contentId;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class RequestReportList {
        @NotNull(message ="데이터 타입은 비면 안됩니다.")
        @Schema(description = "필수값입니다.")
        private StaticDataType staticDataType;
        @NotNull(message = "contentid는 비면안됩니다.")
        @Schema(description = "필수값입니다.")
        private Long contentId;
        @NotNull(message = "offset은 비면안됩니다.")
        @Schema(description = "필수값입니다.")
        private int offset;

        @Builder
        public RequestReportList(StaticDataType staticDataType, Long contentId, int offset) {
            this.staticDataType = staticDataType;
            this.contentId = contentId;
            this.offset = offset;
        }

        public int getOffset() {
            return this.offset - 1;
        }

    }


}
