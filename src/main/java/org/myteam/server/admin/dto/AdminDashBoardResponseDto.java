package org.myteam.server.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.myteam.server.admin.utill.StaticDataType;

import java.util.Map;

public record AdminDashBoardResponseDto() {

    @Getter
    @Builder
    public static class ResponseStatic {

        @Schema(description = "기간에 따른 데이터의 양을 보여줍니다.",
                example = "{2025.02.06 : 1}")
        private Map<String, Long> currentStaticData;
        private Long currentCount;
        private Long pastCount;
        private Long totCount;
        @Schema(examples = "-100,100")
        private int percent;

    }

    @Getter
    public static class ResponseLatestData {

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
        @Schema(example = "2025.06.06")
        private String createAt;
        @Schema(description = "이값이 true이면 이미 읽은것,아니면은 읽지않은것입니다.")
        private boolean checkRead;

        public ResponseLatestData(String reportType, StaticDataType staticDataType,
                                  String mainStatus, String subStatus, Long contentId,
                                  String name, String content, String createAt) {
            this.reportType = reportType;
            this.staticDataType = staticDataType;
            this.mainStatus = mainStatus;
            this.subStatus = subStatus;
            this.contentId = contentId;
            this.name = name;
            this.content = content;
            this.createAt = createAt;
        }

        public void updateCreateAt(String createAt) {
            this.createAt = createAt;
        }

        public void mappingCheckRead(boolean check) {
            this.checkRead = check;
        }

        public void updateMainStatus(String val) {
            this.mainStatus = val;
        }

    }

}
