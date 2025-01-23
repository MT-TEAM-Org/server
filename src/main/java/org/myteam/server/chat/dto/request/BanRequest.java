package org.myteam.server.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.chat.domain.BanReason;

import java.util.List;

/**
 * 밴 요청을 받기 위한 DTO
 */
@Getter
@NoArgsConstructor
public class BanRequest {

    private String username; // 밴할 사용자명
    private List<BanReason> reasons;   // 밴 사유
    private String message; // 그때의 채팅
}