package org.myteam.server.report.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.support.IntegrationTestSupport;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Transactional
class ReportDeleteFailureTest extends IntegrationTestSupport {

    @Autowired
    private ReportService reportService;

    @Test
    @DisplayName("없는 신고 삭제 시 예외 발생")
    void deleteReport_notFound() {
        assertThrows(PlayHiveException.class, () ->
                reportService.deleteReport(9999L)
        );
    }
}
