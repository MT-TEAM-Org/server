package org.myteam.server.chat.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.chat.domain.Ban;
import org.myteam.server.chat.domain.BanReason;
import org.myteam.server.chat.dto.request.BanRequest;
import org.myteam.server.chat.dto.response.BanResponse;
import org.myteam.server.chat.repository.BanRepository;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.util.slack.service.SlackService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BanService {

    private final BanRepository banRepository;
    private final SlackService slackService;

    /**
     * 유저 밴 적용
     */
    public BanResponse banUser(BanRequest request) {
        String username = request.getUsername();
        log.info("This user: {} has received a blocking request.", username);
        // 이미 밴된 유저인지 확인
        if (banRepository.existsByUsername(username)) {
            throw new PlayHiveException(ErrorCode.BAN_ALREADY_EXISTS);
        }

        Ban ban = Ban.createBan(username, request.getReasons(), request.getMessage());
        Ban savedBan = banRepository.save(ban);

        String reasons = request.getReasons()
                .stream()
                .map(BanReason::getReason)
                .collect(Collectors.joining(", "));
        String message = String.format("%s (차단 사유: %s)", username, reasons);
        slackService.sendSlackNotification(message);

        return toBanResponse(savedBan);
    }

    /**
     * 유저 밴 해제 (삭제)
     */
    public String unbanUser(String username) {
        log.info("This user: {} has received a unblocking request.", username);

        Ban ban = banRepository.findByUsername(username)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.BAN_NOT_FOUND));

        banRepository.delete(ban);

        return ban.getUsername();
    }

    /**
     * 특정 유저 밴 정보 조회
     */
    public BanResponse findBanByUsername(String username) {
        log.info("find ban user: {}", username);

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
