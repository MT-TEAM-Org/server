package org.myteam.server.chat.block.repository;

import java.util.List;
import java.util.UUID;

public interface MemberBlockQueryRepository {
    List<UUID> findBlockedMemberIdsByBlockerPublicId(UUID blockerPublicId);
}