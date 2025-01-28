package org.myteam.server.chat.domain;

public enum BanReason {
    HARASSMENT("회원에 대한 상습 비방"),
    SEXUAL_CONTENT("음란하거나 성적인 댓글"),
    POLITICAL_CONTENT("정치인 관련 댓글"),
    PROMOTIONAL_OR_ILLEGAL_ADS("홍보성/불법광고 댓글"),
    ETC("기타");

    private String reason;

    BanReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String etcReason) {
        this.reason = getReason() + ": " + etcReason;
    }
}
