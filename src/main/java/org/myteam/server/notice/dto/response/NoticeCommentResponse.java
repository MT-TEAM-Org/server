package org.myteam.server.notice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.myteam.server.member.entity.Member;
import org.myteam.server.notice.domain.NoticeComment;
import org.myteam.server.notice.domain.NoticeReply;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record NoticeCommentResponse() {

    @Data
    @Builder
    @Getter
<<<<<<< HEAD
    @NoArgsConstructor
=======
>>>>>>> e54602e (feat: 공지사항 댓글 기능 추가)
    @AllArgsConstructor
    public static final class NoticeCommentSaveResponse {
        private Long noticeCommentId;
        private Long noticeId;
        private String createdIp;
        private UUID publicId;
        private String nickname;
        private String imageUrl;
        private String comment;
        @Setter
        @JsonProperty("isRecommended")
        private boolean isRecommended;
        private int recommendCount;
        private LocalDateTime createDate;
        private LocalDateTime lastModifiedDate;
<<<<<<< HEAD
        @Setter
        private List<NoticeReplyResponse> noticeReplyList;
=======
        private List<NoticeReplyResponse> boardReplyList;
>>>>>>> e54602e (feat: 공지사항 댓글 기능 추가)

        public static NoticeCommentSaveResponse createResponse(NoticeComment noticeComment, Member member, boolean isRecommended) {
            return NoticeCommentSaveResponse.builder()
                    .noticeCommentId(noticeComment.getId())
                    .noticeId(noticeComment.getNotice().getId())
                    .createdIp(noticeComment.getCreatedIp())
                    .publicId(member.getPublicId())
                    .nickname(member.getNickname())
                    .imageUrl(noticeComment.getImageUrl())
                    .comment(noticeComment.getComment())
                    .isRecommended(isRecommended)
                    .recommendCount(noticeComment.getRecommendCount())
                    .createDate(noticeComment.getCreateDate())
                    .lastModifiedDate(noticeComment.getLastModifiedDate())
                    .build();
        }
    }

    @Data
    @Builder
    @Getter
<<<<<<< HEAD
    @NoArgsConstructor
=======
>>>>>>> e54602e (feat: 공지사항 댓글 기능 추가)
    @AllArgsConstructor
    public static final class NoticeReplyResponse {
        private Long noticeReplyId;
        private Long noticeCommentId;
        private String createdIp;
        private UUID publicId;
        private String nickname;
        private String imageUrl;
        private String comment;
        @Setter
        @JsonProperty("isRecommended")
        private boolean isRecommended;
        private int recommendCount;
        private UUID mentionedPublicId;
        private String mentionedNickname;
        private LocalDateTime createDate;
        private LocalDateTime lastModifiedDate;

        public static NoticeReplyResponse createResponse(NoticeReply noticeReply, Member member,
                                                         Member mentionedMember, boolean isRecommended) {
            return NoticeReplyResponse.builder()
                    .noticeReplyId(noticeReply.getId())
                    .noticeCommentId(noticeReply.getNoticeComment().getId())
                    .createdIp(noticeReply.getCreatedIp())
                    .publicId(member.getPublicId())
                    .nickname(member.getNickname())
                    .imageUrl(noticeReply.getImageUrl())
                    .comment(noticeReply.getComment())
                    .isRecommended(isRecommended)
                    .recommendCount(noticeReply.getRecommendCount())
                    .mentionedPublicId(mentionedMember != null ? mentionedMember.getPublicId() : null)
                    .mentionedNickname(mentionedMember != null ? mentionedMember.getNickname() : null)
                    .createDate(noticeReply.getCreateDate())
                    .lastModifiedDate(noticeReply.getLastModifiedDate())
                    .build();
        }
    }

<<<<<<< HEAD
    @Data
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class NoticeCommentListResponse {
        private long total;
        private List<NoticeCommentSaveResponse> content;

        public static NoticeCommentListResponse createResponse(List<NoticeCommentSaveResponse> content) {
            return NoticeCommentListResponse.builder()
                    .total(content.size())
                    .content(content)
                    .build();
        }
    }
=======
>>>>>>> e54602e (feat: 공지사항 댓글 기능 추가)
}
