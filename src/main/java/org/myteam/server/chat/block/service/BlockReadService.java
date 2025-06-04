package org.myteam.server.chat.block.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.block.dto.response.BlockedMembersResponse;
import org.myteam.server.chat.block.dto.response.BlockedMembersResponse.*;
import org.myteam.server.chat.block.repository.MemberBlockRepository;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class BlockReadService {

    private final MemberBlockRepository memberBlockRepository;

    /**
     * 특정 유저가 차단한 유저 목록 조회
     */
    public BlockedMembersResponse getBlockedUsers(UUID blockerId) {
        log.debug("차단 목록 조회 요청: blockerId={}", blockerId);
        List<UUID> blockedIds = memberBlockRepository.existsByBlockerPublicId(blockerId);
        return BlockedMembersResponse.from(blockedIds);
    }
}
