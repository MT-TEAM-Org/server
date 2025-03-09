package org.myteam.server.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.service.BoardReadService;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.global.util.redis.RedisService;
import org.myteam.server.member.entity.Member;
import org.myteam.server.member.service.MemberReadService;
import org.myteam.server.news.news.service.NewsReadService;
import org.myteam.server.report.domain.DomainType;
import org.myteam.server.report.domain.ReportType;
import org.myteam.server.report.dto.request.ReportRequest.*;
import org.myteam.server.report.domain.Report;
import org.myteam.server.report.dto.response.ReportResponse.*;
import org.myteam.server.report.repository.ReportRepository;
import org.myteam.server.report.util.ReportedContentValidator;
import org.myteam.server.report.util.ReportedContentValidatorFactory;
import org.myteam.server.util.slack.service.SlackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final MemberReadService memberReadService;
    private final RedisService redisService;
    private final SlackService slackService;
    private final ReportedContentValidatorFactory reportedContentValidatorFactory;

    private static final String REPORT_LIMIT_CATEGORY = "IP";

    /**
     * 신고 생성 (중복 신고 방지)\
     */
    public ReportSaveResponse reportContent(ReportSaveRequest request, String reportIp) {

        // IP 기준으로 5분 내 3회 신고 제한
        if (!redisService.isAllowed(REPORT_LIMIT_CATEGORY, reportIp)) {
            long ttl = redisService.getTimeToLive(REPORT_LIMIT_CATEGORY, reportIp);
            throw new PlayHiveException(ErrorCode.REPORT_LIMIT_EXCEEDED, ttl);
        }

        // 신고한 사용자와 신고 대상 가져오기
        Member reporter = memberReadService.findById(request.getReportPublicId());
        Member reported = memberReadService.findById(request.getReportedPublicId());

        // 자신의 게시글 신고 금지
        if (reporter.equals(reported)) {
            throw new PlayHiveException(ErrorCode.INVALID_REPORT_MEMBER);
        }

        // 신고 대상 검증 (Strategy + Factory Pattern 적용)
        DomainType domainType = mapReportTypeToDomainType(request.getReportType());

        ReportedContentValidator validator = reportedContentValidatorFactory.getValidator(domainType);

        if (validator == null) {
            throw new PlayHiveException(ErrorCode.INVALID_REPORT_TYPE);
        }

        if (!validator.isValid(request.getReportedContentId())) {
            throw new PlayHiveException(ErrorCode.REPORTED_CONTENT_NOT_FOUND);
        }

        // 신고 생성
        Report report = Report.createReport(reporter, reported, reportIp, request.getReportType(), request.getReportedContentId(), request.getReasons());

        log.info("✅ [신고 생성] 신고자: {}, 대상: {}, 타입: {}, content: {}, 사유: {}",
                reporter.getPublicId(), reported.getPublicId(), request.getReportType(), request.getReportedContentId(), request.getReasons());

        // 🚀 Slack 알림 전송
        sendSlackNotification(report, reportIp);

        reportRepository.save(report);

        return ReportSaveResponse.createResponse(report);
    }

    private DomainType mapReportTypeToDomainType(ReportType reportType) {
        // TODO: 문의하기, 공지사항 추후 추가
        return switch (reportType) {
            case BOARD, COMMENT, REPLY -> DomainType.BOARD;
            case NEWS, NEWS_COMMENT, NEWS_REPLY -> DomainType.NEWS;
            case INQUIRY, INQUIRY_COMMENT, INQUIRY_REPLY -> DomainType.INQUIRY;
            default -> throw new PlayHiveException(ErrorCode.INVALID_REPORT_TYPE);
        };
    }

    /**
     * 신고 삭제
     */
    public void deleteReport(Long reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new PlayHiveException(ErrorCode.REPORT_NOT_FOUND);
        }
        reportRepository.deleteById(reportId);
        log.info("🗑️ [신고 삭제] 신고 ID: {}", reportId);
    }

    /**
     * 🚀 Slack 알림 메시지 생성 및 전송
     */
    private void sendSlackNotification(Report report, String reportIp) {
        String message = String.format(
                "🚨 신고 발생 🚨\n" +
                        "📌 신고자: `%s`\n" +
                        "📌 신고 대상: `%s`\n" +
                        "📌 신고 유형: `%s`\n" +
                        "📌 신고된 컨텐츠 ID: `%d`\n" +
                        "📌 신고 사유: `%s`\n" +
                        "📌 신고 IP: `%s`",
                report.getReporter().getPublicId(),
                report.getReported().getPublicId(),
                report.getReportType(),
                report.getReportedContentId(),
                report.getReason(),
                reportIp
        );

        slackService.sendSlackNotification(message);
    }
}
