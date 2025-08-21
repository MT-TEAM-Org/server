package org.myteam.server.chat.block.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

public record BlockRequest() {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BlockUserRequest {
        private UUID blockedId;
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
