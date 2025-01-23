package org.myteam.server.oauth2.response;

import org.apache.commons.lang3.StringUtils;
import org.myteam.server.member.domain.GenderType;

import java.time.LocalDate;
import java.util.Map;

import static org.myteam.server.oauth2.constant.OAuth2ServiceProvider.DISCORD;

public class DiscordResponse implements OAuth2Response{

    private final Map<String, Object> attribute;

    public DiscordResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return DISCORD;
    }

    @Override
    public String getProviderId() {
        return StringUtils.defaultString((String) attribute.get("id"), "");
    }

    @Override
    public String getEmail() {
        return StringUtils.defaultString((String) attribute.get("email"), "");
    }

    @Override
    public String getName() {
        return StringUtils.defaultString((String) attribute.get("username"), "");
    }

    @Override
    public String getNickname() {
        return StringUtils.defaultString((String) attribute.get("global_name"), "");
    }

    // 가져올 수 없음
    @Override
    public String getTel() {
        return "";
    }

    // 가져올 수 없음
    @Override
    public LocalDate getBirthdate() {
        return null;
    }

    // 가져올 수 없음
    @Override
    public GenderType getGender() {
        return null;
    }
}
