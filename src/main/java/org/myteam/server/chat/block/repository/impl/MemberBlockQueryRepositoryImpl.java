package org.myteam.server.chat.block.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.myteam.server.chat.block.domain.QMemberBlock;
import org.myteam.server.chat.block.repository.MemberBlockQueryRepository;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MemberBlockQueryRepositoryImpl implements MemberBlockQueryRepository {

    private final JPAQueryFactory queryFactory;

    QMemberBlock memberBlock = QMemberBlock.memberBlock;

    @Override
    public List<UUID> existsByBlockerPublicId(UUID blockerPublicId) {
        return queryFactory
                .select(memberBlock.blocked)
                .from(memberBlock)
                .where(memberBlock.blocker.eq(blockerPublicId))
                .fetch();
    }
}