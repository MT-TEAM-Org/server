package org.myteam.server.notice.Repository;

import static java.util.Optional.ofNullable;
import static org.myteam.server.notice.domain.QNoticeCount.noticeCount;
import static org.myteam.server.notice.domain.QNotice.notice;
import static org.myteam.server.member.entity.QMember.member;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.notice.domain.NoticeSearchType;
import org.myteam.server.notice.dto.response.NoticeResponse.NoticeDto;
import org.myteam.server.util.ClientUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NoticeQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 공지사항 목록 조회
     */
    public Page<NoticeDto> getNoticeList(NoticeSearchType searchType, String search, Pageable pageable) {

        List<NoticeDto> content = queryFactory
                .select(Projections.constructor(NoticeDto.class,
                        notice.id,
                        notice.title,
                        notice.createdIP,
                        notice.imgUrl,
                        member.publicId,
                        member.nickname,
                        noticeCount.commentCount,
                        noticeCount.recommendCount,
                        notice.createDate,
                        notice.lastModifiedDate
                ))
                .from(notice)
                .join(noticeCount).on(noticeCount.notice.id.eq(notice.id))
                .join(member).on(member.eq(notice.member))
                .fetchJoin()
                .where(isSearchTypeLikeTo(searchType, search))
                .orderBy(notice.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = getTotalNoticeCount(searchType, search);

        content.forEach(noticeDto -> {
            noticeDto.setCreatedIp(ClientUtils.maskIp(noticeDto.getCreatedIp()));
        });

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression isSearchTypeLikeTo(NoticeSearchType noticeSearchType, String search) {
        if (search == null || search.isEmpty()) {
            return null;
        }

        return switch (noticeSearchType) {
            case TITLE -> notice.title.like("%" + search + "%");
            case CONTENT -> notice.content.like("%" + search + "%");
            case TITLE_CONTENT -> notice.title.like("%" + search + "%")
                    .or(notice.content.like("%" + search + "%"));
            case NICKNAME -> notice.member.nickname.like("%" + search + "%");
            default -> null;
        };
    }

    private long getTotalNoticeCount(NoticeSearchType searchType, String search) {
        return ofNullable(
                queryFactory
                        .select(notice.count())
                        .from(notice)
                        .where(isSearchTypeLikeTo(searchType, search))
                        .fetchOne()
        ).orElse(0L);
    }
}
