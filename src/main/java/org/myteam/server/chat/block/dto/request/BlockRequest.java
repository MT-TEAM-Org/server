package org.myteam.server.chat.block.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.chat.block.domain.BanReason;

import java.util.List;
import java.util.UUID;

public record BlockRequest() {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class BlockUserRequest {
        private UUID blockedId;
        private List<BanReason> reasons;
        private String message;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class UnblockUserRequest {
        private UUID blockedId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class BlockCheckRequest {
        private UUID blockedId;
    }
}
