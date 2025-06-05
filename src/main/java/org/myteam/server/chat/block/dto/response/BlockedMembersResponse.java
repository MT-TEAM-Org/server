package org.myteam.server.chat.block.dto.response;

import java.util.List;
import java.util.UUID;

public record BlockedMembersResponse(List<UUID> blocked) {

    public static BlockedMembersResponse from(List<UUID> blockedIds) {
        return new BlockedMembersResponse(blockedIds);
    }
}