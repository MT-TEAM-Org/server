package org.myteam.server.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.checkerframework.checker.units.qual.N;
import org.myteam.server.admin.utils.AdminControlType;
import org.myteam.server.admin.utils.StaticDataType;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.member.domain.GenderType;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.report.domain.ReportReason;
import org.myteam.server.report.domain.ReportType;
import org.springframework.cglib.core.Local;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminDetail() {



    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RequestContentDetail {
        @NotNull
        private StaticDataType staticDataType;
        @NotNull
        private Long id;
        @NotNull
        private boolean isReported;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ResponseContentDetail {

         private AdminControlType adminControlType;
         private String nickName;
         private StaticDataType staticDataType;
         private String content;
         private LocalDateTime createTime;
         private MemberStatus memberStatus;
         private Boolean isReported;
    }





    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RequestClient {
        @NotNull
        private StaticDataType staticDataType;
        private ImprovementStatus improvementStatus;
        @NotNull
        private Long id;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ResponseClient {
        private String improvementStatus;
        private String account;
        private String title;
        private LocalDateTime createAt;
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RequestUserDetail {
        @NotNull
        private UUID uuid;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ResponseUserDetail {
        private String userId;
        private String nickName;
        private String email;
        private String tel;
        private GenderType genderType;
        private MemberType memberType;
        private int birthYear;
        private int birthMonth;
        private int birthDay;
        private LocalDateTime createAt;
        private MemberStatus memberStatus;
        private String content;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RequestUserReportDetail {
        @NotNull
        private Long reportId;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ResponseUserReportDetail {
        private Long reportId;
        private ReportType reportType;
        private String content;
        private BanReason banReason;
        private String reportedReason;
        private LocalDateTime createAt;

    }





}
