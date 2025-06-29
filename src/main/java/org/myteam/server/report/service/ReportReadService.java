package org.myteam.server.report.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.report.domain.Report;
import org.myteam.server.report.domain.ReportType;
import org.myteam.server.report.dto.response.ReportResponse.*;
import org.myteam.server.report.repository.ReportQueryRepository;
import org.myteam.server.report.repository.ReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportReadService {

    private final ReportRepository reportRepository;
    private final ReportQueryRepository reportQueryRepository;

    /**
     * @brief: 해당 서비스는 기획적으로 만들어지진 않았지만 일단 만들어 놓은 서비스입니다.
     */

    /**
     * 🚀 관리자용 신고 목록 조회 (페이징)
     */
    public Page<ReportSaveResponse> getReports(Pageable pageable) {
        return reportQueryRepository.getAllReport(pageable);
    }

    /**
     * 🚀 특정 신고 단건 조회
     */
    public ReportSaveResponse getReportById(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new PlayHiveException(ErrorCode.REPORT_NOT_FOUND));
        return ReportSaveResponse.createResponse(report);
    }

    /**
     * 🚀 내가 받은 신고 내역 조회 (페이징)
     */
    public Page<ReportSaveResponse> getReceivedReports(UUID reportedPublicId, Pageable pageable) {
        return reportQueryRepository.getReportsByReportedUser(reportedPublicId, pageable);
    }

    /**
     * 🚀 내가 보낸 신고 내역 조회 (페이징)
     */
    public Page<ReportSaveResponse> getSentReports(UUID reporterPublicId, Pageable pageable) {
        return reportQueryRepository.getReportsByReporter(reporterPublicId, pageable);
    }




    /**
    * 🚀 가장 최근 신고 리스트 구해오기
    * */

    public Page<ReportSaveResponse> getLatestReportList(){


        Pageable pageable= PageRequest.of(0,10);


       return reportQueryRepository.getAllReport(pageable);


    }


}
