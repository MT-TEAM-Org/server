package org.myteam.server.global.util.redis;

public class CommonCount<T> {
    private final T count;

    private int viewCount;
    private int commentCount;

    public CommonCount(T count, int viewCount, int commentCount) {
        this.count = count;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
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

}