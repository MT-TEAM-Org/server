package org.myteam.server.global.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    BASEBALL("야구"),
    ESPORTS("E스포츠"),
    FOOTBALL("축구"),
    UNKNOWN("에러");

    private final String text;

    public boolean isEsports() {
        return this.equals(ESPORTS);
    }
}
