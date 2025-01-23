package org.myteam.server.member.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
}
