package org.myteam.server.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardSaveRequest {
    /**
     * 카테고리 id
     */
    @NotNull(message = "카테고리를 선택해주세요")
    private Long categoryId;
    /**
     * 제목
     */
    @NotBlank(message = "제목을 입력해주세요")
    private String title;
    /**
     * 내용
     */
    @NotBlank(message = "내용을 입력해주세요")
    private String content;
    /**
     * 출처 링크
     */
    private String link;
}