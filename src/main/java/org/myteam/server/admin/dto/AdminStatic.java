package org.myteam.server.admin.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.myteam.server.admin.utils.DateType;
import org.myteam.server.admin.utils.StaticDataType;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.chat.block.domain.BanReason;
import org.myteam.server.improvement.domain.ImprovementOrderType;
import org.myteam.server.report.domain.ReportReason;
import org.myteam.server.report.domain.ReportType;

import java.time.LocalDateTime;

public record AdminStatic() {



    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RequestStaticData{
        @NotNull
        private StaticDataType staticDataType;
        @NotNull
        private DateType dateType;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ResponseStaticData{
        private Long now_count;
        private Long past_count;
        private int percent;
        private Long tot_count;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class RequestLatestData {
        @NotNull
        private StaticDataType staticDataType;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class LatestData {

        private StaticDataType staticDataType;
        private Long  reportedContentId;
        private String content;
        private String nickName;
        private LocalDateTime createdAt;
    }





}
