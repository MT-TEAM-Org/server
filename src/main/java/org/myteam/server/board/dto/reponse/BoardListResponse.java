package org.myteam.server.board.dto.reponse;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.notice.dto.response.NoticeResponse.NoticeDto;

@Getter
@NoArgsConstructor
public class BoardListResponse {

    /**
     * 공지사항
     */
    private List<NoticeDto> noticeList;
    private PageCustomResponse<BoardDto> list;

    @Builder
    public BoardListResponse(List<NoticeDto> noticeList, PageCustomResponse<BoardDto> list) {
        this.noticeList = noticeList;
        this.list = list;
    }

    public static BoardListResponse createResponse(List<NoticeDto> noticeList, PageCustomResponse<BoardDto> list) {
        return BoardListResponse.builder()
                .noticeList(noticeList)
                .list(list)
                .build();
    }
}