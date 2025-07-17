package org.myteam.server.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.utill.DateType;
import org.myteam.server.admin.utill.StaticDataType;

public record AdminBashBoardRequestDto() {

    @Getter
    @NoArgsConstructor
    @Schema(name = "RequestStatic", description = "대시보드 통계 요청")
    public static class RequestStatic {
        @NotNull
        @Schema(description = "데이터 타입입니다." + " 게시물/댓글의 경우 BoardComment를" +
                "                \"개선사항/문의 는 ImprovementInquiry 를 해주시면 됩니다.", examples = "    Report, ReportedChat, ReportedComment, ReportedBoard,\n" +
                "    BOARD, COMMENT, BoardComment,\n" +
                "    Improvement, Inquiry, ImprovementInquiry,\n" +
                "    UserAccess, UserSignIn, UserDeleted, UserWarned, UserBanned,\n" +
                "    HideComment, HideBoard")
        private StaticDataType staticDataType;

        @NotNull
        @Schema(description = "기간 종류", examples = "Day,WeekEnd,OneMonth,ThreeMonth,SixMonth,Year" +
                "중 택1")
        private DateType dateType;

        @Builder
        public RequestStatic(StaticDataType staticDataType, DateType dateType) {
            this.staticDataType = staticDataType;
            this.dateType = dateType;
        }

    }

    @Getter
    @NoArgsConstructor
    @Schema(name = "RequestLatestData", description = "최신 데이터 요청")
    public static class RequestLatestData {
        @NotNull
        @Schema(description = "통계 데이터 타입" + "게시물/댓글의 경우 BoardComment를" +
                "개선사항/문의 는 ImprovementInquriy를 해주시면 됩니다.", examples = "    Report, ReportedChat, ReportedComment, ReportedBoard,\n" +
                "    BOARD, COMMENT, BoardComment,\n" +
                "    Improvement, Inquiry, ImprovementInquiry,\n" +
                "    UserAccess, UserSignIn, UserDeleted, UserWarned, UserBanned,\n" +
                "    HideComment, HideBoard")
        private StaticDataType staticDataType;

        @Builder
        public RequestLatestData(StaticDataType staticDataType) {
            this.staticDataType = staticDataType;

        }

    }

}
