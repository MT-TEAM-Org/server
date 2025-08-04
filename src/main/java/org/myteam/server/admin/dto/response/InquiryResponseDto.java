package org.myteam.server.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public record InquiryResponseDto() {

    @Getter
    @AllArgsConstructor
    public final static class ResponseInquiryList {
        private Long id;
        @Schema(example ="답변대기,답변완료")
        private String processStatus;
        @Schema(example = "회원,비회원")
        private String isMember;
        private String nickName;
        private String email;
        private String content;
        private String createDate;

        public void updateCreateDate(String date) {
            this.createDate = date;
        }

    }

    @Getter
    @AllArgsConstructor
    public final static class ResponseInquiryListCond {
        private Long id;
        @Schema(example ="답변대기,답변완료")
        private String isAnswered;
        @Schema(example = "회원,비회원")
        private String isMember;
        @Schema(description ="문의 작성자의 닉네임입니다. 닉네임이없다면 메일로 들어갑니다.")
        private String nicknameEmail;
        private String content;
        @Schema(description = "문의 작성자의 메일입니다.")
        private String userMail;
        private String createDate;

        public void updateCreateDate(String date) {
            this.createDate = date;
        }
    }
    @Getter
    public final static class ResponseInquiryDetail {
        private Long inquiryId;
        private String isAnswered;
        private String createDate;
        private String ip;
        private String isMember;
        private String nickName;
        private String email;
        private String content;
        private List<CommonResponseDto.AdminMemoResponse> adminMemoResponseList;

        public ResponseInquiryDetail(Long inquiryId, String isAnswered, String createDate,
                                     String ip, String isMember, String nickName, String email,
                                     String content) {
            this.inquiryId = inquiryId;
            this.isAnswered = isAnswered;
            this.createDate = createDate;
            this.ip = ip;
            this.isMember = isMember;
            this.nickName = nickName;
            this.email = email;
            this.content = content;
        }

        public void updateAdminMemoList(List<CommonResponseDto.AdminMemoResponse> adminMemoResponseList) {
            this.adminMemoResponseList = adminMemoResponseList;
        }

        public void updateCreateDate(String date) {
            this.createDate = date;
        }
    }
}
