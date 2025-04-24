package org.myteam.server.improvement.repository;

import static java.util.Optional.ofNullable;
import static org.myteam.server.comment.domain.QImprovementComment.improvementComment;
import static org.myteam.server.improvement.domain.QImprovement.improvement;
import static org.myteam.server.improvement.domain.QImprovementCount.improvementCount;
import static org.myteam.server.member.entity.QMember.member;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.dto.reponse.CommentSearchDto;
import org.myteam.server.comment.domain.CommentType;
import org.myteam.server.comment.domain.QComment;
import org.myteam.server.comment.domain.QImprovementComment;
import org.myteam.server.global.util.redis.CommonCountDto;
import org.myteam.server.global.util.redis.RedisCountService;
import org.myteam.server.global.util.redis.ServiceType;
import org.myteam.server.improvement.domain.ImprovementOrderType;
import org.myteam.server.improvement.domain.ImprovementSearchType;
import org.myteam.server.improvement.dto.response.ImprovementResponse.ImprovementDto;
import org.myteam.server.improvement.dto.response.ImprovementResponse.ImprovementRankingDto;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.util.ClientUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ImprovementQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final RedisCountService redisCountService;

    /**
     * 개선요청 목록 조회
     */
    public Page<ImprovementDto> getImprovementList(ImprovementOrderType orderType,
                                                   ImprovementSearchType searchType, String search, Pageable pageable) {
        List<ImprovementDto> content = queryFactory
                .select(Projections.fields(ImprovementDto.class,
                        improvement.id,
                        improvement.id.in(getHotImprovementIdList()).as("isHot"),
                        improvement.title,
                        improvement.createdIp.as("createdIp"),
                        improvement.imgUrl.as("thumbnail"),
                        improvement.improvementStatus.as("status"),
                        improvement.member.publicId,
                        improvement.member.nickname,
                        improvementCount.commentCount,
                        improvementCount.recommendCount,
                        improvement.createDate.as("createdAt"),
                        improvement.lastModifiedDate.as("lastModifiedDate")
                ))
                .from(improvement)
                .join(improvementCount).on(improvementCount.improvement.id.eq(improvement.id))
                .join(member).on(member.eq(improvement.member))
                .fetchJoin()
                .where(isSearchTypeLikeTo(searchType, search))
                .orderBy(isOrderByEqualToOrderCategory(orderType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        for (ImprovementDto improvementDto : content) {
            improvementDto.setCreatedIp(ClientUtils.maskIp(improvementDto.getCreatedIp()));
            CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK, DomainType.IMPROVEMENT,
                    improvementDto.getId(), null);
            improvementDto.setRecommendCount(commonCountDto.getRecommendCount());
            improvementDto.setCommentCount(commonCountDto.getCommentCount());
        }

        long total = getTotalImprovementCount(searchType, search);

        if (searchType == ImprovementSearchType.COMMENT) {
            content.forEach(improvementDto -> {
                CommentSearchDto commentSearch = getSearchImprovementComment(improvementDto.getId(), search);
                improvementDto.setImprovementCommentSearchList(commentSearch);
            });
        }

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression isSearchTypeLikeTo(ImprovementSearchType searchType, String search) {
        if (search == null || search.isEmpty()) {
            return null;
        }

        return switch (searchType) {
            case TITLE -> improvement.title.like("%" + search + "%");
            case CONTENT -> improvement.content.like("%" + search + "%");
            case TITLE_CONTENT -> improvement.title.like("%" + search + "%")
                    .or(improvement.content.like("%" + search + "%"));
            case NICKNAME -> improvement.member.nickname.like("%" + search + "%");
            case COMMENT -> JPAExpressions.selectOne()
                    .from(QComment.comment1)
                    .where(
                            QComment.comment1.comment.like("%" + search + "%")
                                    .and(QComment.comment1.commentType.eq(CommentType.IMPROVEMENT))
                                    .and(QComment.comment1.as(QImprovementComment.class).improvement.id.eq(
                                            improvement.id))
                    )
                    .exists();
            default -> null;
        };
    }

    private OrderSpecifier<?> isOrderByEqualToOrderCategory(ImprovementOrderType orderType) {
        // default 최신순
        ImprovementOrderType improvementOrderType = Optional.ofNullable(orderType).orElse(ImprovementOrderType.CREATE);

        return switch (improvementOrderType) {
            case CREATE -> improvement.createDate.desc();
            case RECOMMEND -> improvementCount.recommendCount.desc();
            case COMMENT -> improvementCount.commentCount.desc();
        };
    }

    private CommentSearchDto getSearchImprovementComment(Long improvementId, String search) {
        JPQLQuery<CommentSearchDto> query = queryFactory
                .select(Projections.fields(CommentSearchDto.class,
                        improvementComment.id.as("commentId"),
                        improvementComment.comment,
                        improvementComment.imageUrl
                ))
                .from(improvementComment)
                .where(
                        improvementComment.improvement.id.eq(improvementId),
                        improvementComment.comment.like("%" + search + "%")
                );

        return query.orderBy(
                improvementComment.createDate.desc(),
                improvementComment.comment.asc()
        ).fetchFirst();
    }

    private long getTotalImprovementCount(ImprovementSearchType searchType, String search) {
        return ofNullable(
                queryFactory
                        .select(improvement.count())
                        .from(improvement)
                        .where(isSearchTypeLikeTo(searchType, search))
                        .fetchOne()
        ).orElse(0L);
    }

    private List<Long> getHotImprovementIdList() {
        List<Tuple> improvements = queryFactory
                .select(
                        improvement.id,
                        improvement.title,
                        improvementCount.recommendCount,
                        improvementCount.commentCount
                )
                .from(improvement)
                .join(improvementCount).on(improvementCount.improvement.id.eq(improvement.id))
                .fetch();

        List<Long> result = improvements.stream()
                .map(tuple -> {
                    Long improvementId = tuple.get(improvement.id);
                    CommonCountDto commonCountDto = redisCountService.getCommonCount(ServiceType.CHECK,
                            DomainType.IMPROVEMENT,
                            improvementId, null);
                    int viewCount = commonCountDto.getViewCount();
                    int recommendCount = commonCountDto.getRecommendCount();
                    int commentCount = commonCountDto.getCommentCount();
                    String title = tuple.get(improvement.title);

                    return new ImprovementRankingDto(improvementId, viewCount, recommendCount, commentCount, title,
                            viewCount + recommendCount);
                })
                .sorted(Comparator.comparing(ImprovementRankingDto::getRecommendCount).reversed()
                        .thenComparing(ImprovementRankingDto::getTotalScore).reversed()
                        .thenComparing(ImprovementRankingDto::getId)

                )
                .limit(10)
                .map(ImprovementRankingDto::getId)
                .collect(Collectors.toList());

        return result;
    }

    public Long findPreviousImprovementId(Long improvementId) {
        return queryFactory
                .select(improvement.id)
                .from(improvement)
                .where(improvement.id.lt(improvementId))
                .orderBy(improvement.id.desc()) // 가장 큰 ID (즉, 이전 글)
                .limit(1)
                .fetchOne();
    }


    public Long findNextImprovementId(Long improvementId) {
        return queryFactory
                .select(improvement.id)
                .from(improvement)
                .where(improvement.id.gt(improvementId))
                .orderBy(improvement.id.asc()) // 가장 작은 ID (즉, 다음 글)
                .limit(1)
                .fetchOne();
    }
}
