package org.myteam.server.chat.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.myteam.server.chat.domain.BanReason;

import java.util.List;

/**
 * 밴 정보를 반환하기 위한 DTO
 */
@Getter
@Builder
public class BanResponse {
    private Long id;
    private String username;
    private List<BanReason> reason;
    private String bannedAt;
}