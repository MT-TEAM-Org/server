package org.myteam.server.board.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryUpdateRequest {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z가-힣0-9_\\-\\s]{1,20}$", message = "한글/영문 1~20자 이내로 작성해주세요")
    private String name;

    // 수정 시 무조건 순번이 존재한다고 가정
    @NotNull
    @Min(value = 1, message = "순서는 1 이상이어야 합니다.")
    private Integer orderIndex;

    private String link;
}