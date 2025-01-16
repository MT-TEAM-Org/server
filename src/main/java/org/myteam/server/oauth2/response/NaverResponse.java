package org.myteam.server.oauth2.response;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.myteam.server.member.domain.GenderType;
import org.myteam.server.member.domain.validator.MemberValidator;

import java.time.LocalDate;
import java.util.Map;

import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.myteam.server.member.domain.GenderType.F;
import static org.myteam.server.member.domain.GenderType.M;
import static org.myteam.server.oauth2.constant.OAuth2ServiceProvider.NAVER;

@Slf4j
public class NaverResponse implements OAuth2Response{
    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getProvider() {
        return NAVER;
    }

    @Override
    public String getProviderId() {
        return StringUtils.defaultString((String) attribute.get("id"), null);
    }

    @Override
    public String getEmail() {
        return StringUtils.defaultString((String)attribute.get("email"), null);
    }

    @Override
    public String getName() {
        return StringUtils.defaultString((String) attribute.get("name"), "");
    }

    @Override
    public String getNickname() {
        return StringUtils.defaultString((String) attribute.get("nickname"), "");
    }

    @Override
    public String getTel() {
        String phoneNumber = StringUtils.defaultString((String) attribute.get("mobile"), "").replace("-", "");
        return MemberValidator.validateTel(phoneNumber);
    }

    @Override
    public LocalDate getBirthdate() {
        String birthday = StringUtils.defaultString((String) attribute.get("birthday"), "");
        // toInt is safe: null,"03", "003", "a", "", " "
        int birthyear = toInt((String) attribute.get("birthyear"), 0);

        if (birthyear != 0 && StringUtils.isNotBlank(birthday)) {
            String[] split = birthday.split("-");
            if (split.length == 2) {
                int month = toInt(split[0], 0);
                int day = toInt(split[1], 0);

                // month, day validation 체크
                if (month > 0 && month <= 12 && day > 0 && day <= 31) {
                    return LocalDate.of(birthyear, month, day);
                }
            }
        }
        return null;
    }

    @Override
    public GenderType getGender() {
        String gender = StringUtils.defaultString((String) attribute.get("gender"), "");
        if (gender.equalsIgnoreCase(F.name())) {
            return F; // 남성
        } else if (gender.equalsIgnoreCase(M.name())) {
            return M; // 여성
        }
        return null;
    }
}
