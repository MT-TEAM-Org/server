package org.myteam.server.member.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;

import java.util.UUID;

@Getter
public class MemberResponse {
    private Long id;

    private String email; // 계정

    private String tel;

    private String nickname;

    private MemberRole role;

    private MemberType type;

    private MemberStatus status;

    @JsonIgnore
    private UUID publicId;

    public MemberResponse() {
    }

    @Builder
    public MemberResponse(final Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.tel = member.getTel();
        this.nickname = member.getNickname();
        this.role = member.getRole();
        this.type = member.getType();
        this.status = member.getStatus();
        this.publicId = member.getPublicId();
    }
}
