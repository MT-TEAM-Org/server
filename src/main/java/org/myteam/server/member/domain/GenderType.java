package org.myteam.server.member.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;

@AllArgsConstructor
@Getter
public enum GenderType {
    M("MALE"), F("FEMALE");
    private String value;

    public static GenderType fromValue(String value) {
        for (GenderType gender : GenderType.values()) {
            if (gender.getValue().equalsIgnoreCase(value)) {
                return gender;
            }
        }
        return null;
    }

    public static void validateGender(GenderType genderType) {
        if (!genderType.equals(GenderType.F) && !genderType.equals(GenderType.M)) {
            throw new PlayHiveException(ErrorCode.INVALID_GENDER_TYPE);
        }
    }
}
