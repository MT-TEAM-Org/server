package org.myteam.server.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.myteam.server.admin.utill.DateType;
import org.myteam.server.admin.utill.StaticDataType;
import org.myteam.server.admin.utill.StaticUtil;
import org.myteam.server.report.domain.ReportType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record AdminDashBorad() {

    @Getter
    @NoArgsConstructor
    @Schema(name = "RequestStatic", description = "대시보드 통계 요청")
    public static final class RequestStatic{
        @NotNull
        @Schema(description = "통계 데이터 타입입니다."+" 게시물/댓글의 경우 BoardComment를" +
                "                \"개선사항/문의 는 ImprovementInquiry 를 해주시면 됩니다.",examples ="\"Report,ReportedChat,ReportedComment\"+\n" +
                "                \"ReportedBoard,Board,Comment,BoardComment\"+\n" +
                "                \"Improvement,Inquiry,ImprovementInquiry\"+\n" +
                "                \"UserAccess,UserSignIn,UserDeleted")
        private StaticDataType staticDataType;
        @NotNull
        @Schema(description = "기간 종류",examples ="Day,WeekEnd,OneMonth,ThreeMonth,SixMonth,Year" +
                "중 택1")
        private DateType dateType;

        @Builder
        public RequestStatic(StaticDataType staticDataType,DateType dateType){
            this.staticDataType=staticDataType;
            this.dateType=dateType;
        }

    }
    @Getter
    @Builder
    public static final class ResponseStatic{

        @Schema(description = "기간에 따른 데이터의 양을 보여줍니다. 2025.02.06 : 1 이런꼴입니다.")
        private Map<String,Long> currentStaticData;
        private Long currentCount;
        private Long pastCount;
        private Long totCount;
        private int percent;

    }


    @Getter
    @NoArgsConstructor
    @Schema(name = "RequestLatestData", description = "최신 데이터 요청")
    public static final class RequestLatestData{
        @NotNull
        @Schema(description = "통계 데이터 타입"+"게시물/댓글의 경우 BoardComment를" +
                "개선사항/문의 는 ImprovementInquriy를 해주시면 됩니다.",examples ="Report,ReportedChat,ReportedComment"+
                "ReportedBoard,Board,Comment,BoardComment"+
                "Improvement,Inquiry,ImprovementInquiry"+
                "UserAccess,UserSignIn,UserDeleted")
        private StaticDataType staticDataType;


        @Builder
        public RequestLatestData(StaticDataType staticDataType){
            this.staticDataType=staticDataType;

        }

    }
    @Getter
    public static final class ResponseLatestData{

        @Schema(description = "불러온 데이터 타입이 신고일때 어떤 콘텐츠에대한 신고인지를 나타냅니다.")
        private String reportType;
        private StaticDataType staticDataType;
        @Schema(description = "최신 데이터를 관리자단의 우측 하단에서 표시할때 가장 왼쪽에오는 상태값입니다.")
        private String mainStatus;
        @Schema(description = "최신 데이터를 관리자단의 우측 하단에서 표시할때 왼쪽에서 두번째에 해당하는 상태값입니다.")
        private String subStatus;
        private Long contentId;
        private String name;
        private String content;
        private LocalDateTime createAt;
        @Schema(description = "이값이 true이면 이미 읽은것,아니면은 읽지않은것입니다.")
        private boolean checkRead;
        public ResponseLatestData(String reportType,StaticDataType staticDataType,String mainStatus,String subStatus,Long contentId, String name, String content, LocalDateTime createAt) {
            this.reportType=reportType;
            this.staticDataType=staticDataType;
            this.mainStatus=mainStatus;
            this.subStatus=subStatus;
            this.contentId = contentId;
            this.name = name;
            this.content = content;
            this.createAt = createAt;
        }
        public void mappingCheckRead(boolean check){

            this.checkRead=check;

        }

        public void updateMainStatus(String val){

            this.mainStatus=val;

        }

    }

}
