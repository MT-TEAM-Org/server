package org.myteam.server.game.repository;

import static org.myteam.server.game.domain.QGameEvent.gameEvent;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.game.dto.response.GameEventDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GameQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 게임 이벤트 목록 조회 (노출 날짜가 오늘인 것중 최대 10개 조회)
     */
    public Page<GameEventDto> getGameEventList(Pageable pageable) {
        // 최대 10개만 조회
        List<GameEventDto> content = queryFactory
                .select(Projections.constructor(GameEventDto.class,
                        gameEvent.id,
                        gameEvent.thumbImg,
                        gameEvent.title,
                        gameEvent.description,
                        gameEvent.period,
                        gameEvent.link,
                        gameEvent.exposureDate
                ))
                .from(gameEvent)
                .where(gameEvent.exposureDate.eq(LocalDate.now().atStartOfDay()))
                .orderBy(gameEvent.id.asc())
                .limit(10) // 최대 10개까지만 조회
                .fetch();

        // 전체 개수 (조회된 데이터 개수)
        int total = content.size();

        // 메모리에서 페이징 적용
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);

        List<GameEventDto> pagedContent = content.subList(start, end);

        return new PageImpl<>(pagedContent, pageable, total);
    }
}