package org.myteam.server.chat.block.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.myteam.server.chat.block.domain.entity.MemberBlock;

import java.util.UUID;

public record BlockResponse() {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class SuccessBlockResponse {
        private UUID blocker;
        private UUID blocked;

        public static SuccessBlockResponse createBlockResponse(MemberBlock block) {
            return SuccessBlockResponse.builder()
                    .blocker(block.getBlocker().getPublicId())
                    .blocked(block.getBlocked().getPublicId())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class BlockedMemberInfo {
        private UUID blocked;
    }
}
