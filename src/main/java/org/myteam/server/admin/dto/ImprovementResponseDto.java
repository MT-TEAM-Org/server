package org.myteam.server.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public record ImprovementResponseDto() {

    @Getter
    @AllArgsConstructor
    public final static class ResponseImprovement {
        private Long improvementId;
        private UUID publicId;
        private String importantStatus;
        private String processStatus;
        private Integer recommendCount;
        private String nickName;
        private String title;
        private String content;
        private String createDate;

        public void updateCreateDate(String date) {
            this.createDate = date;
        }
    }

    @Getter
    public final static class ResponseImprovementDetail {
        private String nickName;
        private String createDate;
        private String ip;
        private String title;
        private String content;
        private String improvementStatus;
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
    @AllArgsConstructor
    public final static class ResponseMemberImproveList {
        private Long id;
        private String improvementStatus;
        private String importantDegree;
        private Integer recommendCount;
        private String nickName;
        private String title;
        private String content;
        private String createDate;

        public void updateCreateDate(String date) {
            this.createDate = date;
        }
    }
}
