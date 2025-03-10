package org.myteam.server.home.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HomeBoardListResponse {

    /**
     * 실시간 HOT 게시글 목록
     */
    private List<HotBoardDto> hotBoardList;
    /**
     * 실시간 최신 게시글 목록
     */
    private List<NewBoardDto> newBoardList;

    @Builder
    public HomeBoardListResponse(List<HotBoardDto> hotBoardList, List<NewBoardDto> newBoardList) {
        this.hotBoardList = hotBoardList;
        this.newBoardList = newBoardList;
    }

    public static HomeBoardListResponse createResponse(List<HotBoardDto> hotBoardList,
                                                       List<NewBoardDto> newBoardList) {
        return HomeBoardListResponse.builder()
                .hotBoardList(hotBoardList)
                .newBoardList(newBoardList)
                .build();
    }
}