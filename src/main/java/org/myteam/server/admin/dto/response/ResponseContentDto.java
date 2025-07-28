package org.myteam.server.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

import static org.myteam.server.admin.dto.response.CommonResponseDto.*;

public record ResponseContentDto() {
    @Getter
    @AllArgsConstructor
    public static class ResponseContentSearch {
        private Long contentId;
        private String nickName;
        @Schema(example = "게시판,댓글,채팅")
        private String staticDataType;
        private String content;
        @Schema(example = "2025.06.06")
        private String createDate;
        @Schema(example = "정지,정상,경고")
        private String memberStatus;
        @Schema(example = "노출,보류,숨김")
        private String adminControlType;
        private Long reportCount;
        @Schema(description = "신고 됐다면 신고 신고가 없다면 미신고로 표시")
        private String reported;

        public void updateCountReported(Long count, String reported) {
            this.reported = reported;
            this.reportCount = count;
        }

        public void updateCreateDate(String date) {
            this.createDate = date;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class CountSearch {
        private Long contentId;
        private String contentType;
        private Long reportCount;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseDetail {
        @Schema(example = "노출,보류,숨김")
        private String adminControlType;
        @Schema(example = "신고,미신고")
        private String reported;
        private Long reportCount;
        private Integer recommendCount;
        @Schema(example = "2025.06.06/16:30")
        private String createDate;
        @Schema(example = "정지,정상,경고")
        private String memberStatus;
        private String nickname;
        private String link;
        private String title;
        private String content;
        private String ip;
        private List<AdminMemoResponse> adminMemoResponses;

        public ResponseDetail(String adminControlType, Long reportCount, Integer recommendCount,
                              String createDate, String memberStatus,
                              String nickname, String title, String content, String ip
                , String link) {
            this.adminControlType = adminControlType;
            this.reportCount = reportCount;
            this.recommendCount = recommendCount;
            this.createDate = createDate;
            this.memberStatus = memberStatus;
            this.nickname = nickname;
            this.link = link;
            this.title = title;
            this.content = content;
            this.ip = ip;
        }

        public void updateReported(String reported) {
            this.reported = reported;
        }

        public void updateAdminMemoResponses(List<AdminMemoResponse> adminMemoResponses) {
            this.adminMemoResponses = adminMemoResponses;
        }

        public void updateCreateDate(String date) {
            this.createDate = date;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ResponseReportList {
        private String nickName;
        @Schema(example = "욕설,풍기위반,정치,경고,광고,기타")
        private String reportType;
        private String content;
        @Schema(example = "2025.06.06")
        private String createDate;

        @Builder
        public ResponseReportList(String nickName, String reportType, String content, String createDate) {
            this.nickName = nickName;
            this.reportType = reportType;
            this.content = content;
            this.createDate = createDate;
        }

        public void updateCreateDate(String date) {
            this.createDate = date;
        }

    }

}
