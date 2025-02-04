package org.myteam.server.board.domain;

public enum BoardType {
    /**
     * e-sports
     */
    E_SPORTS,
    /**
     * 야구
     */
    BASEBALL,
    /**
     * 축구
     */
    FOOTBALL;

    public boolean isEsports() {
        return this.equals(E_SPORTS);
    }
}