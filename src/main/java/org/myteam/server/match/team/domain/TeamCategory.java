package org.myteam.server.match.team.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamCategory {
    BASEBALL("야구"),
    ESPORTS("E스포츠"),
    FOOTBALL("축구");

    private final String text;
}
