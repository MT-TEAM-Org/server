package org.myteam.server.chat.block.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.block.domain.MemberBlock;
import org.myteam.server.chat.block.dto.request.BlockRequest.*;
import org.myteam.server.chat.block.dto.response.BlockResponse.*;
import org.myteam.server.chat.block.repository.MemberBlockRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.member.service.SecurityReadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class BlockService {

    private final MemberBlockRepository memberBlockRepository;
    private final SecurityReadService securityReadService;
    private final MemberReadService memberReadService;

    /**
     * 유저 밴 적용
     */
    public SuccessBlockResponse banUser(BlockUserRequest request) {
        Member blocker = securityReadService.getMember();
        Member blocked = memberReadService.findById(request.getBlockedId());
        log.info("This user: {} has received a blocking request.", blocker.getPublicId());

        // 이미 밴된 유저인지 확인
        if (memberBlockRepository.existsByBlockerPublicIdAndBlockedPublicId(blocker.getPublicId(), request.getBlockedId())) {
            log.error("This user: {} is already ban this user: {}", blocker.getPublicId(), blocked.getPublicId());
            throw new PlayHiveException(ErrorCode.BAN_ALREADY_EXISTS);
        }

        MemberBlock block = MemberBlock.createMemberBlock(blocker, blocked, request.getReasons(), request.getMessage());
        memberBlockRepository.save(block);

        return SuccessBlockResponse.createBlockResponse(block);
    }

    /**
     * 차단 해제
     */
    public void unblockUser(UUID blockedId) {
        Member blocker = securityReadService.getMember();
        Member blocked = memberReadService.findById(blockedId);
        log.info("This user: {} has received a unblocking request.", blocker.getPublicId());

        MemberBlock block = memberBlockRepository.findByBlockerPublicIdAndBlockedPublicId(
                        blocker.getPublicId(), blocked.getPublicId())
                .orElseThrow(() -> {
                    log.error("This user: {} is not banned this user: {}", blocker.getPublicId(), blocked.getPublicId());
                    throw new PlayHiveException(ErrorCode.BAN_NOT_FOUND);
                });

        memberBlockRepository.delete(block);
    }
}
