package org.myteam.server.global.util.redis;

import lombok.Getter;

@Getter
public class CommonCountDto {
    private int viewCount;
    private int commentCount;
    private int recommendCount;

    public CommonCountDto(int viewCount, int commentCount, int recommendCount) {
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.recommendCount = recommendCount;
    }
}
