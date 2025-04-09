package org.myteam.server.oauth2.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.myteam.server.member.domain.MemberType;

@Getter
public class AddMemberInfoRequest {
    @NotNull
    private String email;
    @NotNull
    private MemberType memberType;
    @NotNull
    private String tel;
    @NotNull
    private String nickname;
}
