package org.myteam.server.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.util.date.DateFormatUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.myteam.server.admin.dto.CommonResponseDto.AdminMemoResponse;

public record RequestInquiryDto() {


    @NoArgsConstructor
    @Getter
    public final static class RequestInquiryList {
        @NotNull
        private UUID publicId;
        @NotNull
        private int offset;

        @Builder
        public RequestInquiryList(UUID publicId, int offset) {
            this.publicId = publicId;
            this.offset = offset;
        }

        public int getOffset() {
            return this.offset - 1;
        }
    }


    @Getter
    @NoArgsConstructor
    public final static class RequestInquiryListCond {
        private Boolean isMember;
        private String startTime;
        private String endTime;
        private Boolean isAnswered;
        private String nickName;
        private String email;
        private String content;
        @NotNull
        private int offset;


        @Builder
        public RequestInquiryListCond(Boolean isMember, String startTime, String endTime,
                                      Boolean isAnswered, String nickName, String email, String content
                , int offset) {
            this.isMember = isMember;
            this.startTime = startTime;
            this.endTime = endTime;
            this.isAnswered = isAnswered;
            this.nickName = nickName;
            this.email = email;
            this.content = content;
            this.offset = offset;
        }

        public int getOffset() {
            return this.offset - 1;
        }

        public LocalDateTime provideStartTime() {
            if (startTime == null) {
                return null;
            }
            LocalDate localDate = LocalDate.parse(startTime, DateFormatUtil.formatByDot);
            return localDate.atStartOfDay();
        }

        public LocalDateTime provideEndTime() {
            if (endTime == null) {
                return null;
            }
            LocalDate localDate = LocalDate.parse(endTime, DateFormatUtil.formatByDot);
            return localDate.atStartOfDay();
        }
    }


    @Getter
    @NoArgsConstructor
    public final static class RequestInquiryDetail {
        @NotNull
        private Long id;

        @Builder
        public RequestInquiryDetail(Long id) {
            this.id = id;
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
        private List<AdminMemoResponse> adminMemoResponseList;

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

        public void updateAdminMemoList(List<AdminMemoResponse> adminMemoResponseList) {
            this.adminMemoResponseList = adminMemoResponseList;
        }

        public void updateCreateDate(String date) {
            this.createDate = date;
        }
    }


    @Getter
    @NoArgsConstructor
    public final static class RequestInquiryAnswer {
        private Long inquiryId;
        private String email;
        private String content;
        private String createDate;

        public void updateCreateDate(String date) {
            this.createDate = date;
        }
    }

}
