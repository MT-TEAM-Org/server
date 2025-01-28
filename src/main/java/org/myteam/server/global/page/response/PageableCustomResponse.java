package org.myteam.server.global.page.response;

import org.springframework.data.domain.Page;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PageableCustomResponse {
    private int currentPage;
    private int totalPage;
    private long totalElement;

    @Builder
    private PageableCustomResponse(int currentPage, int totalPage, long totalElement) {
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        this.totalElement = totalElement;
    }

    public static <T> PageableCustomResponse of(Page<T> page) {
        return PageableCustomResponse.builder()
            .currentPage(page.getNumber() + 1)
            .totalPage(page.getTotalPages())
            .totalElement(page.getTotalElements())
            .build();
    }
}
