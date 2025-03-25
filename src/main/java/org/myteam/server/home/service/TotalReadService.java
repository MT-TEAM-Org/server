package org.myteam.server.home.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.dto.reponse.BoardDto;
import org.myteam.server.board.repository.BoardQueryRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.page.response.PageCustomResponse;
import org.myteam.server.home.dto.HotBoardDto;
import org.myteam.server.home.dto.NewBoardDto;
import org.myteam.server.home.dto.TotalListResponse;
import org.myteam.server.home.dto.TotalSearchServiceRequest;
import org.myteam.server.news.news.dto.repository.NewsDto;
import org.myteam.server.news.news.repository.NewsQueryRepository;
import org.myteam.server.report.domain.DomainType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TotalReadService {
    private final BoardQueryRepository boardQueryRepository;
    private final NewsQueryRepository newsQueryRepository;

    public List<HotBoardDto> getHotBoardList() {
        return boardQueryRepository.getHotBoardList();
    }

    public List<NewBoardDto> getNewBoardList() {
        return boardQueryRepository.getNewBoardList();
    }

    public TotalListResponse getTotalList(TotalSearchServiceRequest totalSearchServiceRequest) {
        if (totalSearchServiceRequest.getDomainType() == DomainType.NEWS) {
            Page<NewsDto> newsPagingList = newsQueryRepository.getTotalList(
                    totalSearchServiceRequest.getTimePeriod(),
                    totalSearchServiceRequest.getOrderType(),
                    totalSearchServiceRequest.getSearchType(),
                    totalSearchServiceRequest.getSearch(),
                    totalSearchServiceRequest.toPageable()
            );
            return TotalListResponse.createResponse(PageCustomResponse.of(newsPagingList));
        } else if (totalSearchServiceRequest.getDomainType() == DomainType.BOARD) {
            Page<BoardDto> boardPagingList = boardQueryRepository.getTotalList(
                    totalSearchServiceRequest.getTimePeriod(),
                    totalSearchServiceRequest.getOrderType(),
                    totalSearchServiceRequest.getSearchType(),
                    totalSearchServiceRequest.getSearch(),
                    totalSearchServiceRequest.toPageable()
            );

            return TotalListResponse.createResponse(PageCustomResponse.of(boardPagingList));
        } else {
            throw new PlayHiveException(ErrorCode.INVALID_TYPE);
        }
    }
}
