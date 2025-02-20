package org.myteam.server.notice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myteam.server.notice.domain.Notice;
import org.myteam.server.notice.domain.NoticeCount;

import java.time.LocalDateTime;
import java.util.UUID;

public record NoticeResponse() {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public final class NoticeSaveResponse {
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

        public static NoticeSaveResponse createResponse(Notice notice, NoticeCount noticeCount, boolean isRecommended) {
            return NoticeSaveResponse.builder()
                    .noticeId(notice.getId())
                    .publicId(notice.getMember().getPublicId())
                    .nickname(notice.getMember().getNickname())
                    .clientIp(notice.getCreatedIP())
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .imgUrl(notice.getImgUrl())
                    .isRecommended(isRecommended)
                    .recommendCount(noticeCount.getRecommendCount())
                    .commentCount(noticeCount.getCommentCount())
                    .viewCount(noticeCount.getViewCount())
                    .createdAt(notice.getCreateDate())
                    .modifiedAt(notice.getLastModifiedDate())
                    .build();
        }
    }
}
