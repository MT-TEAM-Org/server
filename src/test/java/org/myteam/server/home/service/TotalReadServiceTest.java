package org.myteam.server.home.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.dto.reponse.BoardDto;
import org.myteam.server.board.repository.BoardQueryRepository;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.home.dto.HotBoardDto;
import org.myteam.server.home.dto.NewBoardDto;
import org.myteam.server.home.dto.TotalListResponse;
import org.myteam.server.home.dto.TotalSearchServiceRequest;
import org.myteam.server.news.news.dto.repository.NewsDto;
import org.myteam.server.news.news.repository.NewsQueryRepository;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TotalReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private TotalReadService totalReadService;

    @MockBean
    private BoardQueryRepository boardQueryRepository;

    @MockBean
    private NewsQueryRepository newsQueryRepository;

    @Test
    @DisplayName("핫 게시글 리스트 조회 성공")
    void getHotBoardList_success() {
        // given
        List<HotBoardDto> hotBoardList = List.of(
                new HotBoardDto(1, Category.BASEBALL, CategoryType.FREE, 1L, "Hot Board 1",
                        10, true, false, false),
                new HotBoardDto(2, Category.ESPORTS, CategoryType.ISSUE, 2L, "Hot Board 2",
                        8, true, false, false)
        );
        when(boardQueryRepository.getHotBoardList()).thenReturn(hotBoardList);

        // when
        List<HotBoardDto> result = totalReadService.getHotBoardList();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Hot Board 1");
    }

    @Test
    @DisplayName("신규 게시글 리스트 조회 성공")
    void getNewBoardList_success() {
        // given
        List<NewBoardDto> newBoardList = List.of(
                new NewBoardDto(4L, Category.BASEBALL, CategoryType.FREE, "New Board 1",
                        10, true, true, false),
                new NewBoardDto(4L, Category.ESPORTS, CategoryType.ISSUE, "New Board 2",
                        8, false, true, false)
        );
        when(boardQueryRepository.getNewBoardList()).thenReturn(newBoardList);

        // when
        List<NewBoardDto> result = totalReadService.getNewBoardList();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(1).getTitle()).isEqualTo("New Board 2");
    }

    @Test
    @DisplayName("Total 리스트 조회 성공 - DomainType이 NEWS인 경우")
    void getTotalList_newsDomain() {
        // given
        TotalSearchServiceRequest request = TotalSearchServiceRequest.builder()
                .domainType(DomainType.NEWS)
                .timePeriod(null)
                .orderType(null)
                .searchType(null)
                .search(null)
                .page(1)
                .size(10)
                .build();

        Page<NewsDto> newsPage = new PageImpl<>(List.of(
                new NewsDto(1L, Category.BASEBALL, "뉴스 1", null, "내용 1",
                        8, LocalDateTime.now().minusDays(5L), false),
                new NewsDto(2L, Category.BASEBALL, "뉴스 2", null, "내용 2",
                        10, LocalDateTime.now().minusDays(2L), true)
        ), request.toPageable(), 2);

        when(newsQueryRepository.getTotalList(
                any(), any(), any(), any(), any(Pageable.class))
        ).thenReturn(newsPage);

        // when
        TotalListResponse response = totalReadService.getTotalList(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getList().getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Total 리스트 조회 성공 - DomainType이 BOARD인 경우")
    void getTotalList_boardDomain() {
        // given
        TotalSearchServiceRequest request = TotalSearchServiceRequest.builder()
                .domainType(DomainType.BOARD)
                .timePeriod(null)
                .orderType(null)
                .searchType(null)
                .search(null)
                .page(1)
                .size(10)
                .build();

        Page<BoardDto> boardPage = new PageImpl<>(List.of(
                new BoardDto(Category.ESPORTS, CategoryType.ISSUE, 1L, false,true,
                        "게시글 1","127.0.0.1", null, null,
                        "test1", 10, 8, LocalDateTime.now().minusDays(5L),  LocalDateTime.now().minusDays(5L)),
                new BoardDto(Category.ESPORTS, CategoryType.FREE, 2L, false,true,
                        "게시글 2","127.0.0.1", null, null,
                        "test2", 15, 3, LocalDateTime.now().minusDays(3L),  LocalDateTime.now().minusDays(3L))
        ), request.toPageable(), 2);

        when(boardQueryRepository.getTotalList(
                any(), any(), any(), any(), any(Pageable.class))
        ).thenReturn(boardPage);

        // when
        TotalListResponse response = totalReadService.getTotalList(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getList().getContent()).hasSize(2);
    }

    @Test
    @DisplayName("Total 리스트 조회 실패 - DomainType이 NEWS, BOARD가 아닌 경우")
    void getTotalList_invalidDomain() {
        // given
        TotalSearchServiceRequest request = TotalSearchServiceRequest.builder()
                .domainType(DomainType.NONE) // NEWS, BOARD가 아님
                .timePeriod(null)
                .orderType(null)
                .searchType(null)
                .search(null)
                .page(0)
                .size(10)
                .build();

        // when & then
        assertThatThrownBy(() -> totalReadService.getTotalList(request))
                .isInstanceOf(PlayHiveException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TYPE);
    }
}