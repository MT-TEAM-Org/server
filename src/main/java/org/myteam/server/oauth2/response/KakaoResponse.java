package org.myteam.server.oauth2.response;

import org.apache.commons.lang3.StringUtils;
import org.myteam.server.member.domain.GenderType;
import org.myteam.server.member.domain.validator.MemberValidator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.myteam.server.member.domain.GenderType.F;
import static org.myteam.server.member.domain.GenderType.M;
import static org.myteam.server.oauth2.constant.OAuth2ServiceProvider.KAKAO;

/**
 https://developers.kakao.com/console/app/1171473/product/login/scope
 https://developers.kakao.com/tool/rest-api/open/get/v2-user-me
 https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info (사용자 정보 가져오기 참고)
 */
public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        System.out.println("Raw Kakao attribute: " + attribute);
        System.out.println("Kakao account: " + kakaoAccount(attribute));
        System.out.println("Kakao profile: " + kakaoProfile(attribute));
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return KAKAO;
    }

    public Map<String, Object> kakaoAccount() {
        return kakaoAccount(this.attribute);
    }

    private Map<String, Object> kakaoAccount(Map<String, Object> attr) {
        return (Map<String, Object>) attr.getOrDefault("kakao_account", Collections.emptyMap());
    }

    public Map<String, Object> kakaoProfile() {
        return kakaoProfile(this.attribute);
    }

    private Map<String, Object> kakaoProfile(Map<String, Object> attr) {
        return (Map<String, Object>) kakaoAccount(attr).getOrDefault("profile", Collections.emptyMap());
    }

    @Override
    public String getProviderId() {
        return StringUtils.defaultString((String) attribute.get("id"), null);
    }

    @Override
    public String getEmail() {
        return StringUtils.defaultString((String) kakaoAccount().get("email"), null);
    }

    @Override
    public String getName() {
        return StringUtils.defaultString((String) kakaoAccount().get("name"), null);
    }

    @Override
    public String getNickname() {
        return StringUtils.defaultString((String) kakaoProfile().get("nickname"), null);
    }

    /**
     * 카카오계정의 전화번호 : phone_number ["+82 010-1234-5678"]
     * 국내 번호인 경우 +82 00-0000-0000 형식
     * 해외 번호인 경우 자릿수, 붙임표(-) 유무나 위치가 다를 수 있음
     * https://github.com/google/libphonenumber 참고
     * @return
     */
    @Override
    public String getTel() {
        // given
        String phoneNumber = StringUtils.defaultString((String) attribute.get("phone_number"), "")
                .replaceAll("^\\+\\d{1,3}\\s{1,}", "") // 국가 코드 제거 (예: +82, +1 등)
                .replace("-", "")                  // 하이픈 제거
                .replace(" ", "");                 // 공백 제거

        return MemberValidator.validateTel(phoneNumber);
    }

    /**
     * 출생연도: birthyear ["2002"]
     * 출생일 : birthday ["1103"]
     * @return
     */
    @Override
    public LocalDate getBirthdate() {
        String birthday = StringUtils.defaultString((String) attribute.get("birthday"), "");
        // toInt is safe: null,"03", "003", "a", "", " "
        int birthyear = toInt((String) attribute.get("birthyear"), 0);

        if (birthyear != 0 && StringUtils.isNotBlank(birthday)) {
            if (StringUtils.isNotBlank(birthday) && birthday.length() > 3) {
                int month = toInt(birthday.substring(0, 2), 0);
                int day = toInt(birthday.substring(2, 4), 0);

                // month, day validation 체크
                if (month > 0 && month <= 12 && day > 0 && day <= 31) {
                    return LocalDate.of(birthyear, month, day);
                }
            }
        }
        return null;
    }

    /**
     * 성별
     * @return [female, male]
     */
    @Override
    public GenderType getGender() {
        String gender = StringUtils.defaultString((String) attribute.get("gender"), "").trim().toUpperCase();;
        if (gender.startsWith(F.name())) {
            return F; // 남성
        } else if (gender.startsWith(M.name())) {
            return M; // 여성
        }
        return null;
    }

    public String getProfileImageUrl() {
        return (String) kakaoProfile().get("profile_image_url");
    }

    public String getThumbnailImageUrl() {
        return (String) kakaoProfile().get("thumbnail_image_url");
    }
}
