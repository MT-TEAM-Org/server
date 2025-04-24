package org.myteam.server.global.util.redis;

public class CommonCount<T> {
    private final T count;

    private int viewCount;
    private int commentCount;
    private int recommendCount;

    public CommonCount(T count, int viewCount, int commentCount) {
        this.count = count;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
    }

    public CommonCount(T count, int viewCount, int commentCount, int recommendCount) {
        this.count = count;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.recommendCount = recommendCount;
    }

    public T getCount() {
        return count;
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getRecommendCount() {
        return recommendCount;
    }
}