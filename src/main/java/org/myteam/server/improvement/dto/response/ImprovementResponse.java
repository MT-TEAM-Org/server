package org.myteam.server.improvement.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;
import org.myteam.server.board.dto.reponse.CommentSearchDto;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.improvement.domain.Improvement;
import org.myteam.server.improvement.domain.ImprovementCount;
import org.myteam.server.improvement.domain.ImprovementStatus;
import org.myteam.server.util.ClientUtils;

public record ImprovementResponse() {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ImprovementSaveResponse {
        private Long noticeId; // 개선요청 id
        private UUID publicId; // 작성자 id
        private String nickname; // 작성자 닉네임
        private String clientIp; // 작성자 IP
        private String title; // 게시글 제목
        private String content; // 게시글 내용
        private String imgUrl; // 이미지 url
        private ImprovementStatus status;
        @JsonProperty("isRecommended")
        private boolean isRecommended;
        private int recommendCount; // 추천 수
        private int commentCount; // 댓글 수
        private int viewCount; // 조회 수
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        /**
         * 이전글
         */
        private Long previousId;
        /**
         * 다음글
         */
        private Long nextId;
        private String link;

        public static ImprovementSaveResponse createResponse(Improvement improvement, ImprovementCount improvementCount,
                                                             boolean isRecommended, Long previousId, Long nextId, int viewCount) {
            return ImprovementSaveResponse.builder()
                    .noticeId(improvement.getId())
                    .publicId(improvement.getMember().getPublicId())
                    .nickname(improvement.getMember().getNickname())
                    .clientIp(ClientUtils.maskIp(improvement.getCreatedIP()))
                    .title(improvement.getTitle())
                    .content(improvement.getContent())
                    .imgUrl(improvement.getImgUrl())
                    .status(improvement.getImprovementStatus())
                    .isRecommended(isRecommended)
                    .recommendCount(improvementCount.getRecommendCount())
                    .commentCount(improvementCount.getCommentCount())
                    .viewCount(viewCount)
                    .createdAt(improvement.getCreateDate())
                    .modifiedAt(improvement.getLastModifiedDate())
                    .previousId(previousId)
                    .nextId(nextId)
                    .link(improvement.getLink())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    public static final class ImprovementListResponse {
        private PageCustomResponse<ImprovementDto> list;

        @Builder
        public ImprovementListResponse(PageCustomResponse<ImprovementDto> list) {
            this.list = list;
        }

        public static ImprovementListResponse createResponse(PageCustomResponse<ImprovementDto> list) {
            return ImprovementListResponse.builder()
                    .list(list)
                    .build();
        }

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ImprovementDto {
        private Long id;
        private String title;
        private String createdIp;
        private String thumbnail;
        private ImprovementStatus status;
        private UUID publicId;
        private String nickname;
        private Integer commentCount;
        private Integer recommendCount;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedDate;
        @Setter
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private CommentSearchDto improvementCommentSearchList;
    }
}
