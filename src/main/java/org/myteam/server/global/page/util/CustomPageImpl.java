package org.myteam.server.global.page.util;

import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class CustomPageImpl<T> extends PageImpl<T> {
    private final long totalElements;
    private final long totalPages;

    public CustomPageImpl(List<T> content, Pageable pageable, long totalElements, long totalPages) {
        super(content, pageable, totalElements);
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    @Override
    public long getTotalElements() {
        return totalElements;
    }

    @Override
    public int getTotalPages() {
        return (int) totalPages;
    }
}