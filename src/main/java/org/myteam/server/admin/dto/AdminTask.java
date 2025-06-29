package org.myteam.server.admin.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myteam.server.admin.utils.AdminControlType;
import org.myteam.server.admin.utils.StaticDataType;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.member.domain.MemberStatus;

import java.util.UUID;

public record AdminTask() {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RequestHandleInquiryImprovement{
        @NotNull
        private StaticDataType staticDataType;
        @NotNull
        private ImprovementStatus improvementStatus;
        @NotNull
        private Long contentId;
        @NotEmpty
        private String responseContent;

        private String receiverEmail;

    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ResponseHandleInquiryImprovement{
        private boolean taskComplete;

    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RequestHandleContent{
        @NotNull
        private Long contentId;
        @NotNull
        private StaticDataType staticDataType;
        @NotNull
        private AdminControlType adminControlType;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RequestHandleMember{
        @NotNull
        private UUID memberId;
        @NotNull
        private MemberStatus memberStatus;
        @NotNull
        private String content;

    }


}
