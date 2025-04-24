package org.myteam.server.board.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.board.domain.BoardRecommend;
import org.myteam.server.board.domain.CategoryType;
import org.myteam.server.board.dto.reponse.BoardResponse;
import org.myteam.server.board.dto.request.BoardRequest;
import org.myteam.server.global.domain.Category;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.member.entity.Member;
import org.myteam.server.report.domain.DomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.myteam.server.global.exception.ErrorCode.INVALID_TYPE;
import static org.myteam.server.global.exception.ErrorCode.USER_NOT_FOUND;

class SaveBoardServiceTest extends IntegrationTestSupport {

    @Autowired
    private BoardService boardService;
    private Member member;
    private UUID publicId;

    @BeforeEach
    void setUp() {
        member = createMember(0);
        publicId = member.getPublicId();
    }

    @Test
    @DisplayName("isEsports가 true일 때 게시글 등록 정상 수행")
    void saveBoard_esportsCategory_true() {
        // given
        when(securityReadService.getMember()).thenReturn(member);
        when(memberReadService.findById(publicId)).thenReturn(member);
        when(redisCountService.getCommonCount((ServiceType.CHECK), DomainType.BOARD, 1L, null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        BoardRequest request = new BoardRequest(Category.ESPORTS, CategoryType.FREE,
                "테스트 제목", "테스트 내용", "http://example.com", "http://example.com/thumb.png");

        // when
        BoardResponse response = boardService.saveBoard(request, "127.0.0.1");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
    }

    @Test
    @DisplayName("isEsports가 false인데 confirmEsports() 예외 발생")
    void saveBoard_esportsCategory_false_throws() {
        // given
        when(securityReadService.getMember()).thenReturn(member);
        when(memberReadService.findById(publicId)).thenReturn(member);
        when(redisCountService.getCommonCount((ServiceType.CHECK), DomainType.BOARD, 1L, null))
                .thenReturn(new CommonCountDto(0, 0, 0));
        BoardRequest request = new BoardRequest(Category.BASEBALL, CategoryType.VERIFICATION,
                "테스트 제목", "테스트 내용", "http://example.com", "http://example.com/thumb.png");

        // when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () -> {
            boardService.saveBoard(request, "127.0.0.1");
        });

        // then
        assertEquals(INVALID_TYPE, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인하지 않은 멤버가 save 요청을 할 때")
    void saveBoard_not_login_user() {
        // given
        when(securityReadService.getMember()).thenThrow(new PlayHiveException(USER_NOT_FOUND));
        BoardRequest request = new BoardRequest(Category.ESPORTS, CategoryType.FREE,
                "테스트 제목", "테스트 내용", "http://example.com", "http://example.com/thumb.png");

        // when
        PlayHiveException exception = assertThrows(PlayHiveException.class, () -> {
            boardService.saveBoard(request, "127.0.0.1");
        });

        // then
        assertEquals(USER_NOT_FOUND, exception.getErrorCode());
    }
}