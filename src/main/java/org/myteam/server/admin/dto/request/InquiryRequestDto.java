package org.myteam.server.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.util.date.DateFormatUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.myteam.server.admin.dto.response.CommonResponseDto.AdminMemoResponse;

public record InquiryRequestDto() {


    @NoArgsConstructor
    @Getter
    public final static class RequestInquiryList {
        @NotNull(message = "email은 필수입니다.")
        @Schema(description = "회원 식별 아이디값,필수입니다.")
        private String email;
        @NotNull(message = "offset은 필수입니다.")
        @Schema(description = "필수입니다.")
        private int offset;

        @Builder
        public RequestInquiryList(String email, int offset) {
            this.email = email;
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
        @Schema(example = "2025.06.06")
        private String startTime;
        @Schema(example = "2025.06.06")
        private String endTime;
        private Boolean isAnswered;
        private String nickName;
        private String email;
        private String content;
        @NotNull(message = "offset은 필수입니다.")
        @Schema(description = "필수입니다.")
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
        @NotNull(message = "contentid 는 비면안됩니다.")
        @Schema(description = "inquiry id값입니다. 필수입니다.")
        private Long contentId;

        @Builder
        public RequestInquiryDetail(Long id) {
            this.contentId = id;
        }
    }



}
