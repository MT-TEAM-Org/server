package org.myteam.server.match.matchSchedule.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatchCategory {
    BASEBALL("야구"),
    ESPORTS("E스포츠"),
    FOOTBALL("축구");

    private final String text;
}
