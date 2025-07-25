package org.myteam.server.notice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;
import org.myteam.server.board.dto.reponse.CommentSearchDto;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.util.ClientUtils;

public record NoticeResponse() {

    @AllArgsConstructor
    @Getter
    public static class AdminNoticeResponse{
        private Long noticeId;
        private String nickName;
        private String createDate;
        private String title;
        private String content;

        public void updateCreateDate(String createDate){
            this.createDate=createDate;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class NoticeSaveResponse {
        private Long noticeId; // 공지사항 id
        private UUID publicId; // 작성자 id
        private String nickname; // 작성자 닉네임
        private String clientIp; // 작성자 IP
        private String title; // 게시글 제목
        private String content; // 게시글 내용
        private String imgUrl; // 이미지 url
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

        public static NoticeSaveResponse createResponse(Notice notice, boolean isRecommended,
                                                        Long previousId, Long nextId, CommonCountDto commonCountDto) {
            return NoticeSaveResponse.builder()
                    .noticeId(notice.getId())
                    .publicId(notice.getMember().getPublicId())
                    .nickname(notice.getMember().getNickname())
                    .clientIp(ClientUtils.maskIp(notice.getCreatedIp()))
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .imgUrl(notice.getImgUrl())
                    .isRecommended(isRecommended)
                    .recommendCount(commonCountDto.getRecommendCount())
                    .commentCount(commonCountDto.getCommentCount())
                    .viewCount(commonCountDto.getViewCount())
                    .createdAt(notice.getCreateDate())
                    .modifiedAt(notice.getLastModifiedDate())
                    .previousId(previousId)
                    .nextId(nextId)
                    .link(notice.getLink())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    public static final class NoticeListResponse {
        private PageCustomResponse<NoticeDto> list;

        @Builder
        public NoticeListResponse(PageCustomResponse<NoticeDto> list) {
            this.list = list;
        }

        public static NoticeListResponse createResponse(PageCustomResponse<NoticeDto> list) {
            return NoticeListResponse.builder()
                    .list(list)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class NoticeDto {
        private Long id;
        @JsonProperty("isHot")
        private boolean isHot;
        private String title;
        private String thumbnail;
        private String createdIp;
        private UUID publicId;
        private String nickname;
        private Integer commentCount;
        private Integer recommendCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        /**
         * 댓글 검색 시 결과
         */
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private CommentSearchDto commentSearchList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class NoticeRankingDto {
        private Long id;
        private int viewCount;
        private int recommendCount;
        private int commentCount;
        private String title;
        private int totalScore; // 댓글수 + 조회수
    }
}
