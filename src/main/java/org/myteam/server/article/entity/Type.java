package org.myteam.server.article.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Type {
    BASEBALL("야구"),
    ESPORTS("E스포츠"),
    FOOTBALL("건강");

    private final String text;
}
