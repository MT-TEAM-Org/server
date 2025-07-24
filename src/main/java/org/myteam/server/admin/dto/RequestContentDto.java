package org.myteam.server.admin.dto;

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
import java.util.UUID;

public record RequestContentDto() {
    @Getter
    @NoArgsConstructor
    public static class RequestContentData {
        private BoardSearchType boardSearchType;
        private String searchKeyWord;
        private StaticDataType staticDataType;
        private Boolean isReported;
        private AdminControlType adminControlType;
        @Schema(example = "2025.06.06")
        private String startTime;
        @Schema(example = "2025.06.06")
        private String endTime;
        @NotNull
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
        @Schema(description = "게시물이면 BOARD 댓글이면 COMMENT 입니다")
        private StaticDataType staticDataType;
        @NotNull
        private Long contentId;

        @Builder
        public RequestDetail(StaticDataType staticDataType, Long contentId) {
            this.staticDataType = staticDataType;
            this.contentId = contentId;
        }
    }


    @Getter
    @NoArgsConstructor
    @Schema(description = "관리자 메모 작성 요청시 쓰이는 값입니다.")
    public static class AdminMemoRequest {
        @NotNull
        private Long contentId;
        @Schema(description = "게시물이면 BOARD 댓글이면 COMMENT 입니다.")
        @NotNull
        private StaticDataType staticDataType;
        @Schema(description = "숨김은 HIDDEN,보류는 PENDING 노출은 SHOW입니다.")
        @NotNull
        private AdminControlType adminControlType;
        @Schema(description = "내용이 없다면 null로 주세요")
        private String content;
        @NotNull
        private UUID publicId;

        @Builder
        public AdminMemoRequest(Long contentId, StaticDataType staticDataType,
                                AdminControlType adminControlType, String content) {
            this.contentId = contentId;
            this.staticDataType = staticDataType;
            this.adminControlType = adminControlType;
            this.content = content;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class RequestReportList {
        @NotNull
        private StaticDataType staticDataType;
        @NotNull
        private Long contentId;
        @NotNull
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
