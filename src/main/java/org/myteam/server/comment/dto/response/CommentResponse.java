package org.myteam.server.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myteam.server.comment.domain.Comment;
import org.myteam.server.util.ClientUtils;

public record CommentResponse() {

    @Data
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
//    @JsonInclude(Include.NON_EMPTY)
    public static final class CommentSaveResponse {
        private Long commentId; // 댓글 id
        // 댓글에 대한 게시글 id는 필요 없지 않을까?
        private String createdIp; // 작성 ip
        private UUID publicId; // 작성자 uuid
        private String nickname; // 작성자 닉네임
        private String commenterImg; // 작성자 프로필 이미지
        private String imageUrl; // 이미지 url
        private String comment; // 댓글 내용
        private boolean isRecommended; // 로그인한 사용자 게시글 댓글 추천 여부
        private int recommendCount; // 추천수
        private UUID mentionedPublicId; // 언급 된 사람
        private String mentionedNickname; // 언급된 사람 닉네임
        private LocalDateTime createDate; // 작성 일시
        private LocalDateTime lastModifiedDate; // 수정 일시
        @Setter
        private List<CommentSaveResponse> replyList; // 대댓글 리스트

        public static CommentSaveResponse createResponse(Comment comment, boolean isRecommended) {
            UUID mentionedPublicId =
                    comment.getMentionedMember() == null ? null : comment.getMentionedMember().getPublicId();
            String mentionedNickname =
                    comment.getMentionedMember() == null ? null : comment.getMentionedMember().getNickname();
            return CommentSaveResponse.builder()
                    .commentId(comment.getId())
                    .createdIp(ClientUtils.maskIp(comment.getCreatedIp()))
                    .publicId(comment.getMember().getPublicId())
                    .nickname(comment.getMember().getNickname())
                    .commenterImg(comment.getMember().getImgUrl())
                    .imageUrl(comment.getImageUrl())
                    .comment(comment.getComment())
                    .isRecommended(isRecommended)
                    .recommendCount(comment.getRecommendCount())
                    .mentionedPublicId(mentionedPublicId)
                    .mentionedNickname(mentionedNickname)
                    .createDate(comment.getCreateDate())
                    .lastModifiedDate(comment.getLastModifiedDate())
                    .build();
        }
    }

    @Data
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class CommentSaveListResponse {
        private long total;
        private List<CommentSaveResponse> content;

        public static CommentSaveListResponse createResponse(List<CommentSaveResponse> content) {
            return CommentSaveListResponse.builder()
                    .total(content.size())
                    .content(content)
                    .build();
        }
    }
}
