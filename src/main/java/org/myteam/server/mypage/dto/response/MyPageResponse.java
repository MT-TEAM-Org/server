package org.myteam.server.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myteam.server.member.domain.GenderType;
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
        private long createdCommentCount;   // 내가 작성한 댓글 개수
        private int createdInquiryCount;   // 내가 작성한 문의 개수
        private int totalInviteCount;      // 내가 초대한 횟수
        private String nickname;           // 닉네임
        private String img;
        private MemberRole role;           // 회원 등급
        private String registeredAt;       // 회원 가입일
        private String registrationMethod; // 가입 방법 (이메일, 소셜 로그인 등)

        public static MemberStatsResponse createResponse(Member member, int postCount, long commentCount, int inquiryCount) {
            return MemberStatsResponse.builder()
                    .totalVisitCount(member.getMemberActivity().getVisitCount())
                    .createdPostCount(postCount)
                    .createdCommentCount(commentCount)
                    .createdInquiryCount(inquiryCount)
                    .totalInviteCount(member.getMemberActivity().getVisitCount())
                    .nickname(member.getNickname())
                    .img(member.getImgUrl())
                    .role(member.getRole())
                    .registeredAt(DateUtils.formatDateTime(member.getCreateDate()))
                    .registrationMethod(member.getType().getValue())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberModifyResponse {
        private String email;
        private String tel;
        private String nickname;
        private GenderType genderType;
        private String birthDate;
        private String imageUrl;

        public static MemberModifyResponse createResponse(Member member) {
            StringBuilder birthDate = new StringBuilder();
            if (member.getBirthYear() != 0) {
                birthDate.append(String.format("%02d", member.getBirthYear()));
                birthDate.append(String.format("%02d", member.getBirthMonth()));
                birthDate.append(String.format("%02d", member.getBirthDay()));
            }

            return MemberModifyResponse.builder()
                    .email(member.getEmail())
                    .tel(member.getTel())
                    .nickname(member.getNickname())
                    .genderType(member.getGenderType())
                    .birthDate(birthDate.toString())
                    .imageUrl(member.getImgUrl())
                    .build();
        }
    }

}
