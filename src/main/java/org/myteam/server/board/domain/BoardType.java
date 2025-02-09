package org.myteam.server.board.domain;

public enum BoardType {
    /**
     * e-sports
     */
    ESPORTS,
    /**
     * 야구
     */
    BASEBALL,
    /**
     * 축구
     */
    FOOTBALL;

    public boolean isEsports() {
        return this.equals(ESPORTS);
    }
}