package org.myteam.server.global.page.response;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PageCustomResponse<T> implements Serializable {
    private List<T> content;
    private PageableCustomResponse pageInfo;

    @Builder
    private PageCustomResponse(List<T> content, PageableCustomResponse pageInfo) {
        this.content = content;
        this.pageInfo = pageInfo;
    }

    public static <T> PageCustomResponse<T> of(Page<T> page) {
        return PageCustomResponse.<T>builder()
            .content(page.getContent())
            .pageInfo(PageableCustomResponse.of(page))
            .build();
    }
}
