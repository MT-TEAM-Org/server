package org.myteam.server.global.util.redis;

public class CommonCount<T> {
    private final T count;
    private int viewCount;

    public CommonCount(T count, int viewCount) {
        this.count = count;
        this.viewCount = viewCount;
    }

    public T getCount() {
        return count;
    }

    public int getViewCount() {
        return viewCount;
    }
}