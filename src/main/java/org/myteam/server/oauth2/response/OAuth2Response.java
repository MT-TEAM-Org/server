package org.myteam.server.oauth2.response;

import org.myteam.server.member.domain.GenderType;

import java.time.LocalDate;

public interface OAuth2Response {
    //제공자 (Ex. naver, google, ...)
    String getProvider();
    //제공자에서 발급해주는 아이디(번호)
    String getProviderId();
    //이메일
    String getEmail();
    //이름
    String getName();
    String getNickname();
    String getTel();
    LocalDate getBirthdate();
    GenderType getGender();
}
