package org.myteam.server.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.utill.DateType;
import org.myteam.server.admin.utill.StaticDataType;

public record AdminDashBoardRequestDto() {

    @Getter
    @NoArgsConstructor
    @Schema(name = "RequestStatic", description = "대시보드 통계 요청")
    public static class RequestStatic {
        @NotNull(message ="데이터 타입값이 비어있습니다.")
        @Schema(description ="데이터 타입입니다.필수입니다.",examples =
                "Report, ReportedChat, ReportedComment, ReportedBoard,\n"+
                "BOARD, COMMENT, BoardComment,\n"+
                "Improvement, Inquiry, ImprovementInquiry,\n"+
                "ImprovementPending,ImprovementComplete,ImprovementReceived,\n"+
                "InquiryPending,InquiryComplete,InquiryMember,InquiryNoMember,\n" +
                "UserAccess, UserSignIn, UserDeleted, UserWarned, UserBanned,\n"+
                "HideComment, HideBoard")
        private StaticDataType staticDataType;

        @NotNull(message = "기간 값이 비어있습니다.")
        @Schema(description = "기간 종류, 필수입니다.", examples = "Day,WeekEnd,OneMonth,ThreeMonth,SixMonth,Year" +
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
        @NotNull(message ="데이터 타입값이 비어있습니다.")
        @Schema(description ="데이터 타입입니다. 필수입니다.",examples = "Report, ReportedChat, ReportedComment, ReportedBoard,\n"+
                "BOARD, COMMENT, BoardComment,\n"+
                "Improvement, Inquiry, ImprovementInquiry,\n"+
                "ImprovementPending,ImprovementComplete,ImprovementReceived,\n"+
                "InquiryPending,InquiryComplete,InquiryMember,InquiryNoMember,\n" +
                "UserAccess, UserSignIn, UserDeleted, UserWarned, UserBanned,\n"+
                "HideComment, HideBoard")
        private StaticDataType staticDataType;

        @Builder
        public RequestLatestData(StaticDataType staticDataType) {
            this.staticDataType = staticDataType;

        }

    }

}
