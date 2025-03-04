package org.myteam.server.game.repository;

import static org.myteam.server.game.domain.QGameDiscount.gameDiscount;
import static org.myteam.server.game.domain.QGameEvent.gameEvent;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.game.dto.response.GameDiscountDto;
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
                .offset(pageable.getOffset())
                .limit(10)
                .fetch();

        long total = getTotalEventCount();

        return new PageImpl<>(content, pageable, total);
    }

    private long getTotalEventCount() {
        // 전체 개수를 구할 때는 limit(10)을 제외하고 실제 데이터의 개수를 계산
        long count = queryFactory
                .select(gameEvent.count())
                .from(gameEvent)
                .where(gameEvent.exposureDate.eq(LocalDate.now().atStartOfDay()))
                .fetchOne();

        // 10개 이상이면 10을 반환
        return Math.min(count, 10L);
    }

    /**
     * 게임 할인 목록 조회 (노출 날짜가 오늘인 것중 최대 10개 조회)
     */
    public Page<GameDiscountDto> getGameDiscountList(Pageable pageable) {
        List<GameDiscountDto> content = queryFactory
                .select(Projections.constructor(GameDiscountDto.class,
                        gameDiscount.id,
                        gameDiscount.thumbImg,
                        gameDiscount.title,
                        gameDiscount.originalPrice,
                        gameDiscount.discountPercent,
                        gameDiscount.finalPrice,
                        gameDiscount.link,
                        gameDiscount.exposureDate
                ))
                .from(gameDiscount)
                .where(gameDiscount.exposureDate.eq(LocalDate.now().atStartOfDay()))
                .orderBy(gameDiscount.id.asc())
                .offset(pageable.getOffset())
                .limit(10)
                .fetch();

        long total = getTotalDiscountCount();

        return new PageImpl<>(content, pageable, total);
    }

    private long getTotalDiscountCount() {
        // 전체 개수를 구할 때는 limit(10)을 제외하고 실제 데이터의 개수를 계산
        long count = queryFactory
                .select(gameDiscount.count())
                .from(gameDiscount)
                .where(gameDiscount.exposureDate.eq(LocalDate.now().atStartOfDay()))
                .fetchOne();

        // 10개 이상이면 10을 반환
        return Math.min(count, 10L);
    }
}
