package org.myteam.server.inquiry.dto.response;

import lombok.*;
import org.myteam.server.inquiry.domain.InquiryComment;
import org.myteam.server.inquiry.domain.InquiryReply;
import org.myteam.server.member.entity.Member;
import org.myteam.server.util.ClientUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record InquiryCommentResponse() {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class InquiryCommentSaveResponse {

        private Long inquiryCommentId; // 문의내역 댓글 id
        private Long inquiryId; // 문의 내역 id
        private String createdIp; // 작성 ip
        private UUID publicId; // 작성자 id
        private String nickname; // 작성자 닉네임
        private String imageUrl; // 이미지 url
        private int recommendCount; // 추천 수
        private String comment; // 댓글 내용
        private LocalDateTime createDate; // 작성 일시

        @Setter
        private List<InquiryReplyResponse> boardReplyList; // 대댓글 목록

        @Builder
        public InquiryCommentSaveResponse(Long inquiryCommentId, Long inquiryId, String createdIp,
                                          UUID publicId, String nickname, String imageUrl,
                                          int recommendCount, String comment, LocalDateTime createDate) {
            this.inquiryCommentId = inquiryCommentId;
            this.inquiryId = inquiryId;
            this.createdIp = createdIp;
            this.publicId = publicId;
            this.nickname = nickname;
            this.imageUrl = imageUrl;
            this.recommendCount = recommendCount;
            this.comment = comment;
            this.createDate = createDate;
        }

        public static InquiryCommentSaveResponse createResponse(InquiryComment inquiryComment, Member member) {
            return InquiryCommentSaveResponse.builder()
                    .inquiryCommentId(inquiryComment.getId())
                    .inquiryId(inquiryComment.getInquiry().getId())
                    .createdIp(ClientUtils.maskIp(inquiryComment.getCreatedIp()))
                    .publicId(member.getPublicId())
                    .nickname(member.getNickname())
                    .imageUrl(inquiryComment.getImageUrl())
                    .comment(inquiryComment.getComment())
                    .recommendCount(inquiryComment.getRecommendCount())
                    .createDate(inquiryComment.getCreateDate())
                    .build();
        }

        public static List<InquiryCommentSaveResponse> convertToResponseList(List<InquiryComment> inquiryComments) {
            return inquiryComments.stream()
                    .map(comment -> InquiryCommentSaveResponse.createResponse(comment, comment.getMember()))
                    .collect(Collectors.toList());
        }
    }

    @Data
    @NoArgsConstructor
    public static final class InquiryCommentListResponse {

        private long total;
        private List<InquiryCommentSaveResponse> content;

        @Builder
        public InquiryCommentListResponse(long total, List<InquiryCommentSaveResponse> list) {
            this.total = total;
            this.content = list;
        }

        public static InquiryCommentListResponse createResponse(List<InquiryCommentSaveResponse> list) {
            return InquiryCommentListResponse.builder()
                    .total(list.size())
                    .list(list)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static final class InquiryReplyResponse {

        /**
         * 문의사항 대댓글 id
         */
        private Long inquiryReplyId;
        /**
         * 문의사항 댓글 id
         */
        private Long inquiryCommentId;
        /**
         * 작성 ip
         */
        private String createdIp;
        /**
         * 작성자 id
         */
        private UUID publicId;
        /**
         * 작성자 닉네임
         */
        private String nickname;
        /**
         * 이미지
         */
        private String imageUrl;
        /**
         * 댓글 내용
         */
        private String comment;
        /**
         * 추천 수
         */
        private int recommendCount;
        /**
         * 멘션 당한 사용자 id
         */
        private UUID mentionedPublicId;
        /**
         * 멘션 당한 사용자 닉네임
         */
        private String mentionedNickname;
        /**
         * 작성 일시
         */
        private LocalDateTime createDate;
        /**
         * 수정 일시
         */
        private LocalDateTime lastModifiedDate;

        @Builder
        public InquiryReplyResponse(Long inquiryReplyId, Long inquiryCommentId, String createdIp, UUID publicId, String nickname,
                                    String imageUrl, String comment, int recommendCount, UUID mentionedPublicId,
                                    String mentionedNickname, LocalDateTime createDate, LocalDateTime lastModifiedDate) {
            this.inquiryReplyId = inquiryReplyId;
            this.inquiryCommentId = inquiryCommentId;
            this.createdIp = createdIp;
            this.publicId = publicId;
            this.nickname = nickname;
            this.imageUrl = imageUrl;
            this.comment = comment;
            this.recommendCount = recommendCount;
            this.mentionedPublicId = mentionedPublicId;
            this.mentionedNickname = mentionedNickname;
            this.createDate = createDate;
            this.lastModifiedDate = lastModifiedDate;
        }

        public static InquiryReplyResponse createResponse(InquiryReply inquiryReply, Member member, Member mentionedMember) {
            return InquiryReplyResponse.builder()
                    .inquiryReplyId(inquiryReply.getId())
                    .inquiryCommentId(inquiryReply.getInquiryComment().getId())
                    .createdIp(ClientUtils.maskIp(inquiryReply.getCreatedIp()))
                    .publicId(member.getPublicId())
                    .nickname(member.getNickname())
                    .imageUrl(inquiryReply.getImageUrl())
                    .comment(inquiryReply.getComment())
                    .recommendCount(inquiryReply.getRecommendCount())
                    .mentionedPublicId(mentionedMember != null ? mentionedMember.getPublicId() : null)
                    .mentionedNickname(mentionedMember != null ? mentionedMember.getNickname() : null)
                    .createDate(inquiryReply.getCreateDate())
                    .lastModifiedDate(inquiryReply.getLastModifiedDate())
                    .build();
        }
    }


}
