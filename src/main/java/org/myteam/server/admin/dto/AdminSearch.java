package org.myteam.server.admin.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.signature.qual.BinaryNameOrPrimitiveType;
import org.myteam.server.admin.utils.AdminControlType;
import org.myteam.server.admin.utils.StaticDataType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.member.domain.GenderType;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.ReportType;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record AdminSearch() {



    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RequestSearchContent{
        private BoardSearchType boardSearchType;
        private String searchKeyWord;
        private StaticDataType staticDataType;
        private boolean reported;
        private AdminControlType adminControlType;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        @NotNull
        private int offset;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ResponseSearchContent{
        private Long contentId;
        private String nickName;
        private StaticDataType staticDataType;
        private String content;
        private LocalDateTime createTime;
        private MemberStatus memberStatus;
        private AdminControlType adminControlType;
        private boolean reported;


    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RequestSearchInquiryImprovement{
        @NotNull
        private StaticDataType staticDataType;
        @NotNull
        private int offset;

    }


        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static final class ResponseSearchInquiryImprovement{
            //private ImprovementStatus improvementStatus;
            private String improvementStatus;
            private Long id;
            private String name;
            private String content;
            private LocalDateTime time;

        }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RequestSearchUserList{
        private String nickName;
        private String email;
        private String phoneNumber;
        private GenderType genderType;
        private MemberType memberType;
        private Integer birthYear;
        private Integer birthMonth;
        private Integer birthDay;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        @NotNull
        private int offset;


        public List<Integer> provideBirthday(){
            return Stream.of(birthYear, birthMonth, birthDay)
                    .map(x->{
                        if(x==null){

                            return 0;

                        }
                        return x;

                    })
                    .collect(Collectors.toList());
        }

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ResponseSearchUserList{
        private String publicId;
        private String nickName;
        private String email;
        private String phoneNumber;
        private GenderType genderType;
        private int birthYear;
        private int birthMonth;
        private int birthDay;
        private LocalDateTime signInDate;
        private MemberStatus memberStatus;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RequestUserReportedList{
        @NotNull
        private UUID uuid;
        @NotNull
        private int offset;

    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ResponseUserReportedList{
        private Long id;
        private ReportType reportType;
        private String content;
        private BanReason banReason;
        private LocalDateTime createAt;

    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RequestReportList {
        @NotNull
        private StaticDataType staticDataType;
        @NotNull
        private Long id;
        @NotNull
        private int offset;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ResponseReportList {
        private StaticDataType staticDataType;
        private Long id;
        private String content;
        private BanReason banReason;
        private LocalDateTime createAt;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ResponseAdminProfile{
        private String email;
        private String imgUrl;
        private String nickName;

    }

}
