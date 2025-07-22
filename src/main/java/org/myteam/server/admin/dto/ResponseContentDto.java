package org.myteam.server.admin.dto;

import lombok.*;

import java.util.List;

public record ResponseContentDto() {
    @Getter
    @AllArgsConstructor
    public static class ResponseContentSearch {
        private Long contentId;
        private String nickName;
        private String staticDataType;
        private String content;
        private String createDate;
        private String memberStatus;
        private String adminControlType;
        private Long reportCount;
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
        private String adminControlType;
        private String reported;
        private Long reportCount;
        private Integer recommendCount;
        private String createDate;
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
        private String reportType;
        private String content;
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

    @AllArgsConstructor
    @Getter
    public static class AdminMemoResponse {
        private String writerName;
        private String createDate;
        private String content;

        public void updateCreateDate(String date) {
            this.createDate = date;
        }
    }
}
