package org.myteam.server.member.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class MemberResponse {

    private String email; // 계정

    private String tel;

    private String nickname;

    private MemberRole role;

    private MemberType type;

    private MemberStatus status;

    private UUID publicId;

    public MemberResponse() {
    }

    public MemberResponse(final Member member) {
        this.email = member.getEmail();
        this.tel = member.getTel();
        this.nickname = member.getNickname();
        this.role = member.getRole();
        this.type = member.getType();
        this.status = member.getStatus();
        this.publicId = member.getPublicId();
    }

    public static MemberResponse createMemberResponse(Member member) {
        return MemberResponse.builder()
                .email(member.getEmail())
                .tel(member.getTel())
                .nickname(member.getNickname())
                .role(member.getRole())
                .type(member.getType())
                .status(member.getStatus())
                .publicId(member.getPublicId())
                .build();
    }
}
