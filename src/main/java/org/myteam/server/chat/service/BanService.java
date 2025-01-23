package org.myteam.server.chat.service;

import lombok.AllArgsConstructor;
import org.myteam.server.chat.domain.Ban;
import org.myteam.server.chat.dto.request.BanRequest;
import org.myteam.server.chat.dto.response.BanResponse;
import org.myteam.server.chat.repository.BanRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
public class BanService {

    private final BanRepository banRepository;

    /**
     * TODO: 슬랙 알람 서비스 적용해야함.
     * 유저 밴 적용
     */
    public BanResponse banUser(BanRequest request) {
        // 이미 밴된 유저인지 확인
        if (banRepository.existsByUsername(request.getUsername())) {
            throw new PlayHiveException(ErrorCode.BAN_ALREADY_EXISTS);
        }

        Ban ban = Ban.createBan(request.getUsername(), request.getReasons(), request.getMessage());
        Ban savedBan = banRepository.save(ban);

        return toBanResponse(savedBan);
    }

    /**
     * 유저 밴 해제 (삭제)
     */
    public String unbanUser(String username) {
        Ban ban = banRepository.findByUsername(username)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BAN_NOT_FOUND));

        banRepository.delete(ban);

        return ban.getUsername();
    }

    /**
     * 특정 유저 밴 정보 조회
     */
    public BanResponse findBanByUsername(String username) {
        Ban ban = banRepository.findByUsername(username)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BAN_NOT_FOUND));

        return toBanResponse(ban);
    }

    /**
     * 밴 유저 조회
     */
    public boolean isBannedUser(String username) {
        if (banRepository.existsByUsername(username)) {
            return true;
        }
        return false;
    }

    private BanResponse toBanResponse(Ban ban) {
        return BanResponse.builder()
                .id(ban.getId())
                .username(ban.getUsername())
                .reason(ban.getReasons())
                .bannedAt(ban.getBannedAt().format(DateTimeFormatter.ofPattern("YYYY-MM-DD")))
                .build();
    }
}
