package org.myteam.server.news.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NewsCategory {
    BASEBALL("야구"),
    ESPORTS("E스포츠"),
    FOOTBALL("건강");

    private final String text;
}
