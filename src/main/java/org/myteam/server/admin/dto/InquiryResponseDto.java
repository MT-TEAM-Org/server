package org.myteam.server.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public record InquiryResponseDto() {

    @Getter
    @AllArgsConstructor
    public final static class ResponseInquiryList {
        private Long id;
        private String processStatus;
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
        private String isAnswered;
        private String isMember;
        private String nicknameEmail;
        private String content;
        private String publicId;
        private String createDate;

        public void updateCreateDate(String date) {
            this.createDate = date;
        }
    }
}
