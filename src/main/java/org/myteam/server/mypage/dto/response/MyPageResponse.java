package org.myteam.server.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.entity.Member;
import org.myteam.server.util.DateUtils;

import java.util.UUID;

public record MyPageResponse() {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberStatsResponse {
        private int totalVisitCount;       // 전체 방문 횟수
        private int createdPostCount;      // 내가 작성한 게시물 개수
        private int createdCommentCount;   // 내가 작성한 댓글 개수
        private int createdInquiryCount;   // 내가 작성한 문의 개수
        private int totalInviteCount;      // 내가 초대한 횟수
        private String nickname;           // 닉네임
        private MemberRole role;           // 회원 등급
        private String registeredAt;       // 회원 가입일
        private String registrationMethod; // 가입 방법 (이메일, 소셜 로그인 등)

        public static MemberStatsResponse createResponse(Member member, int postCount, int commentCount, int inquiryCount) {
            return MemberStatsResponse.builder()
                    .totalVisitCount(member.getMemberActivity().getVisitCount())
                    .createdPostCount(postCount)
                    .createdCommentCount(commentCount)
                    .createdInquiryCount(inquiryCount)
                    .totalInviteCount(member.getMemberActivity().getVisitCount())
                    .nickname(member.getNickname())
                    .role(member.getRole())
                    .registeredAt(DateUtils.formatDateTime(member.getCreateDate()))
                    .registrationMethod(member.getType().getValue())
                    .build();
        }
    }

}
