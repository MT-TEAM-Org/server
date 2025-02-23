package org.myteam.server.improvement.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.security.dto.CustomUserDetails;
import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.improvement.dto.ImprovementRequest.*;
import org.myteam.server.improvement.dto.ImprovementResponse.*;
import org.myteam.server.improvement.service.ImprovementReadService;
import org.myteam.server.improvement.service.ImprovementService;
import org.myteam.server.util.ClientUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api/improvement")
@RequiredArgsConstructor
public class ImprovementController {

    private final ImprovementService improvementService;
    private final ImprovementReadService improvementReadService;

    /**
     * 개선목록 생성
     */
    @PostMapping
    public ResponseEntity<ResponseDto<ImprovementSaveResponse>> saveImprovement(@Valid @RequestBody ImprovementSaveRequest improvementSaveRequest,
                                                                           HttpServletRequest request) {
        String clientIP = ClientUtils.getRemoteIP(request);
        ImprovementSaveResponse response = improvementService.saveImprovement(improvementSaveRequest, clientIP);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선목록 생성 성공",
                response
        ));
    }

    /**
     * 개선목록 수정
     */
    @PutMapping("/{improvementId}")
    public ResponseEntity<ResponseDto<ImprovementSaveResponse>> updateImprovement(@Valid @RequestBody ImprovementSaveRequest improvementSaveRequest,
                                                                             @PathVariable Long improvementId) {
        ImprovementSaveResponse response = improvementService.updateImprovement(improvementSaveRequest, improvementId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선목록 수정 성공",
                response
        ));
    }

    /**
     * 개선목록 삭제
     */
    @DeleteMapping("/{improvementId}")
    public ResponseEntity<ResponseDto<Void>> deleteImprovement(@PathVariable Long improvementId) {
        improvementService.deleteImprovement(improvementId);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선목록 삭제 성공",
                null
        ));
    }

    /**
     * 개선목록 상세 조회
     */
    @GetMapping("/{improvementId}")
    public ResponseEntity<ResponseDto<ImprovementSaveResponse>> getImprovement(@PathVariable Long improvementId,
                                                                          @AuthenticationPrincipal final CustomUserDetails userDetails) {
        ImprovementSaveResponse response = improvementReadService.getImprovement(improvementId, userDetails);
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선목록 조회 성공",
                response
        ));
    }

    /**
     * 개선목록 목록 조회
     */
    @GetMapping
    public ResponseEntity<ResponseDto<ImprovementListResponse>> getImprovementList(@Valid @ModelAttribute ImprovementSearchRequest request) {
        return ResponseEntity.ok(new ResponseDto<>(
                SUCCESS.name(),
                "개선목록 목록 조회",
                improvementReadService.getImprovementList(request.toServiceRequest())
        ));
    }
}
