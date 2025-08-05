package org.myteam.server.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.myteam.server.admin.utill.NeedDateTimeFix;

import java.util.List;
import java.util.UUID;

public record ImprovementResponseDto() {

    @Getter

    public final static class ResponseImprovement extends NeedDateTimeFix {
        private Long improvementId;
        private UUID publicId;
        @Schema(example = "높음 중간 낮음")
        private String importantStatus;
        @Schema(example = "접수 대기 완료")
        private String processStatus;
        private Integer recommendCount;
        private String nickName;
        private String title;
        private String content;
        public ResponseImprovement(Long improvementId, UUID publicId, String importantStatus, String processStatus,
                                   Integer recommendCount, String nickName, String title, String content,String createDate) {
            super(createDate);
            this.improvementId = improvementId;
            this.publicId = publicId;
            this.importantStatus = importantStatus;
            this.processStatus = processStatus;
            this.recommendCount = recommendCount;
            this.nickName = nickName;
            this.title = title;
            this.content = content;
        }
    }
    @Getter
    public final static class ResponseImprovementDetail{
        private String nickName;
        private String createDate;
        private String ip;
        private String title;
        private String content;
        @Schema(example = "접수 대기 완료")
        private String improvementStatus;
        @Schema(example = "높음 중간 낮음")
        private String importantStatus;
        private List<CommonResponseDto.AdminMemoResponse> adminMemoResponseList;
        public ResponseImprovementDetail(String nickName, String createDate, String ip, String title,
                                         String content,
                                         String improvementStatus, String importantStatus) {
            this.nickName = nickName;
            this.createDate = createDate;
            this.ip = ip;
            this.title = title;
            this.content = content;
            this.improvementStatus = improvementStatus;
            this.importantStatus = importantStatus;
        }
        public void updateAdminMemoList(List<CommonResponseDto.AdminMemoResponse> adminMemoResponseList) {
            this.adminMemoResponseList = adminMemoResponseList;
        }
        public void updateCreateDate(String date) {
            this.createDate = date;
        }
    }

    @Getter
    public final static class ResponseMemberImproveList extends NeedDateTimeFix{
        private Long id;
        @Schema(example = "접수 대기 완료")
        private String improvementStatus;
        @Schema(example = "높음 중간 낮음")
        private String importantStatus;
        private Integer recommendCount;
        private String nickName;
        private String title;
        private String content;
        public ResponseMemberImproveList(Long id, String improvementStatus, String importantStatus,
                                         Integer recommendCount, String nickName,
                                         String title, String content,String createDate) {
            super(createDate);
            this.id = id;
            this.improvementStatus = improvementStatus;
            this.importantStatus = importantStatus;
            this.recommendCount = recommendCount;
            this.nickName = nickName;
            this.title = title;
            this.content = content;
        }
    }
}
