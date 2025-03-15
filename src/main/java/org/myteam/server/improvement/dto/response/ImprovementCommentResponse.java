//package org.myteam.server.improvement.dto.response;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.*;
//import org.myteam.server.improvement.domain.ImprovementComment;
//import org.myteam.server.improvement.domain.ImprovementReply;
//import org.myteam.server.member.entity.Member;
//import org.myteam.server.util.ClientUtils;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
//public record ImprovementCommentResponse() {
//
//    @Data
//    @Builder
//    @Getter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static final class ImprovementCommentSaveResponse {
//        private Long improvementCommentId;
//        private Long improvementId;
//        @Setter
//        private String createdIp;
//        private UUID publicId;
//        private String nickname;
//        private String imageUrl;
//        private String comment;
//        @Setter
//        @JsonProperty("isRecommended")
//        private boolean isRecommended;
//        private int recommendCount;
//        private LocalDateTime createDate;
//        private LocalDateTime lastModifiedDate;
//        @Setter
//        private List<ImprovementReplyResponse> improvementReplyList;
//
//        public static ImprovementCommentSaveResponse createResponse(ImprovementComment improvementComment, Member member, boolean isRecommended) {
//            return ImprovementCommentSaveResponse.builder()
//                    .improvementCommentId(improvementComment.getId())
//                    .improvementId(improvementComment.getImprovement().getId())
//                    .createdIp(ClientUtils.maskIp(improvementComment.getCreatedIp()))
//                    .publicId(member.getPublicId())
//                    .nickname(member.getNickname())
//                    .imageUrl(improvementComment.getImageUrl())
//                    .comment(improvementComment.getComment())
//                    .isRecommended(isRecommended)
//                    .recommendCount(improvementComment.getRecommendCount())
//                    .createDate(improvementComment.getCreateDate())
//                    .lastModifiedDate(improvementComment.getLastModifiedDate())
//                    .build();
//        }
//    }
//
//    @Data
//    @Builder
//    @Getter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static final class ImprovementReplyResponse {
//        private Long improvementReplyId;
//        private Long improvementCommentId;
//        private String createdIp;
//        private UUID publicId;
//        private String nickname;
//        private String imageUrl;
//        private String comment;
//        @Setter
//        @JsonProperty("isRecommended")
//        private boolean isRecommended;
//        private int recommendCount;
//        private UUID mentionedPublicId;
//        private String mentionedNickname;
//        private LocalDateTime createDate;
//        private LocalDateTime lastModifiedDate;
//
//        public static ImprovementReplyResponse createResponse(ImprovementReply improvementReply, Member member,
//                                                              Member mentionedMember, boolean isRecommended) {
//            return ImprovementReplyResponse.builder()
//                    .improvementReplyId(improvementReply.getId())
//                    .improvementCommentId(improvementReply.getImprovementComment().getId())
//                    .createdIp(ClientUtils.maskIp(improvementReply.getCreatedIp()))
//                    .publicId(member.getPublicId())
//                    .nickname(member.getNickname())
//                    .imageUrl(improvementReply.getImageUrl())
//                    .comment(improvementReply.getComment())
//                    .isRecommended(isRecommended)
//                    .recommendCount(improvementReply.getRecommendCount())
//                    .mentionedPublicId(mentionedMember != null ? mentionedMember.getPublicId() : null)
//                    .mentionedNickname(mentionedMember != null ? mentionedMember.getNickname() : null)
//                    .createDate(improvementReply.getCreateDate())
//                    .lastModifiedDate(improvementReply.getLastModifiedDate())
//                    .build();
//        }
//    }
//
//    @Data
//    @Builder
//    @Getter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static final class ImprovementCommentListResponse {
//        private long total;
//        private List<ImprovementCommentSaveResponse> content;
//
//        public static ImprovementCommentListResponse createResponse(List<ImprovementCommentSaveResponse> content) {
//            return ImprovementCommentListResponse.builder()
//                    .total(content.size())
//                    .content(content)
//                    .build();
//        }
//    }
//
//}
