package org.myteam.server.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static org.myteam.server.admin.dto.response.CommonResponseDto.*;

public record MemberSearchResponseDto() {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseMemberSearch {
        private String status;
        private String nickName;
        private Long boardCount;
        private Long commentCount;
        private Long reportCount;
        private Integer recommendCount;
        private String genderType;
        private String memberType;
        private String email;
        private String tel;
        private String createDate;

        public void updateCreateDate(String date) {
            this.createDate = date;
        }
    }

    @Getter
    public static class ResponseReportList {
        private Long contentId;
        private String reportedDate;
        private Long reportedCount;
        private String reportType;
        private String content;

        public ResponseReportList(Long contentId, String reportedDate, Long reportedCount,
                                  String reportType, String content) {
            this.contentId = contentId;
            this.reportedDate = reportedDate;
            this.reportedCount = reportedCount;
            this.reportType = reportType;
            this.content = content;
        }

        public void updateReportDate(String date) {
            this.reportedDate = date;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ResponseMemberDetail {
        private String nickName;
        private String email;
        private String tel;
        private String genderType;
        private String memberType;
        private int birthYear;
        private int birthMonth;
        private int birthDay;
        private String createDate;
        @Schema(description = "최근 접속 일자")
        private String accessTime;
        @Schema(description = "최근 접속 ip")
        private String ip;
        private int countVisit;
        private Integer recommendCount;
        private Long countImprovement;
        private Long countBoard;
        private Long countComment;
        private Long reportedCount;
        private Long reportCount;
        private String status;
        @Schema(description = "관리자 들이 해당 회원에 대해서 작성한 메모입니다. 작성일 기준으로 오름차순입니다.")
        private List<AdminMemoResponse> adminMemoResponses;

        public ResponseMemberDetail(String nickName, String email, String tel, String genderType, String memberType, int birthYear, int birthMonth, int birthDay, String createDate, String accessTime, String ip, int countVisit,
                                    Integer recommendCount, Long countImprovement,
                                    Long countBoard, Long countComment, Long reportedCount,
                                    Long reportCount, String status) {
            this.nickName = nickName;
            this.email = email;
            this.tel = tel;
            this.genderType = genderType;
            this.memberType = memberType;
            this.birthYear = birthYear;
            this.birthMonth = birthMonth;
            this.birthDay = birthDay;
            this.createDate = createDate;
            this.accessTime = accessTime;
            this.ip = ip;
            this.countVisit = countVisit;
            this.recommendCount = recommendCount;
            this.countImprovement = countImprovement;
            this.countBoard = countBoard;
            this.countComment = countComment;
            this.reportedCount = reportedCount;
            this.reportCount = reportCount;
            this.status = status;
        }

        public void updateAdminMemoResponse(List<AdminMemoResponse> adminMemoResponses) {
            this.adminMemoResponses = adminMemoResponses;
        }

        public void updateAccessDate(String date) {
            this.accessTime = date;
        }

        public void updateCreateDate(String date) {
            this.createDate = date;
        }
    }

}
