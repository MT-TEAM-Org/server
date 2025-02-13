package org.myteam.server.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.board.domain.BoardType;
import org.myteam.server.board.domain.CategoryType;

@Getter
@NoArgsConstructor
@AllArgsConstructor // TODO: 테스트용 이후에 삭제
public class BoardRequest {
    /**
     * 게시판 타입
     */
    @NotNull(message = "게시판 타입을 선택해주세요")
    private BoardType boardType;
    /**
     * 카테고리 타입
     */
    @NotNull(message = "카테고리를 선택해주세요")
    private CategoryType categoryType;
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
    /**
     * 썸네일 이미지
     */
    private String thumbnail;
}