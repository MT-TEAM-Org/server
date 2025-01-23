package org.myteam.server.member.controller.response;

import lombok.Builder;
import lombok.Getter;
import org.myteam.server.member.domain.GenderType;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Oauth2Member;

import java.time.LocalDate;

@Getter
public class Oauth2MemberResponse {
    private Long id;

    private String email; // 계정

    private String tel;

    private String name;

    private String nickname;

    private GenderType gender;

    private LocalDate birthdate;

    private MemberRole role;

    private MemberType type;

    public Oauth2MemberResponse() {
    }

    @Builder
    public Oauth2MemberResponse(final Oauth2Member oauth2Member) {
        this.id = oauth2Member.getId();
        this.email = oauth2Member.getEmail();
        this.tel = oauth2Member.getTel();
        this.name = oauth2Member.getName();
        this.nickname = oauth2Member.getNickname();
        this.gender = oauth2Member.getGender();
        this.birthdate = oauth2Member.getBirthdate();
        this.role = oauth2Member.getRole();
        this.type = oauth2Member.getType();
    }
}
