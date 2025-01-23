package org.myteam.server.oauth2.response;

import org.apache.commons.lang3.StringUtils;
import org.myteam.server.member.domain.GenderType;

import java.time.LocalDate;
import java.util.Map;

import static org.myteam.server.oauth2.constant.OAuth2ServiceProvider.GOOGLE;

public class GoogleResponse implements OAuth2Response{

    private final Map<String, Object> attribute;

    public GoogleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return GOOGLE;
    }

    @Override
    public String getProviderId() {
        return StringUtils.defaultString((String) attribute.get("sub"), "");
    }

    @Override
    public String getEmail() {
        return StringUtils.defaultString((String) attribute.get("email"), "");
    }

    @Override
    public String getName() {
        String firstName = StringUtils.defaultString((String) attribute.get("family_name"), "");
        String lastName = StringUtils.defaultString((String) attribute.get("given_name"), "");
        return firstName + lastName;
    }

    @Override
    public String getNickname() {
        String googleDisplayName = StringUtils.defaultString((String) attribute.get("name"), "");
        String firstName = StringUtils.defaultString((String) attribute.get("family_name"), "");
        String lastName = StringUtils.defaultString((String) attribute.get("given_name"), "");
        String name = googleDisplayName
                .replaceAll(firstName, "") // 이름 제거
                .replaceAll(lastName, "") // 이름 제거
                .replace("(", "")
                .replace(")", "");
        return name.trim();
    }

    @Override
    public String getTel() {
        return "";
    }

    @Override
    public LocalDate getBirthdate() {
        return null;
    }

    @Override
    public GenderType getGender() {
        return null;
    }
}
