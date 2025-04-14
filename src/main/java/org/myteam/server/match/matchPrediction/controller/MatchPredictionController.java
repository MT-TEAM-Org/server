package org.myteam.server.match.matchPrediction.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;

import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.match.matchPrediction.dto.controller.MatchPredictionRequest;
import org.myteam.server.match.matchPrediction.dto.service.response.MatchPredictionResponse;
import org.myteam.server.match.matchPrediction.service.MatchPredictionReadService;
import org.myteam.server.match.matchPrediction.service.MatchPredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match/prediction")
public class MatchPredictionController {

	private final MatchPredictionReadService matchPredictionReadService;
	private final MatchPredictionService matchPredictionService;

	@Operation(summary = "경기 예측 현황 조회 API", description = "경기예측 현황을 조회한다.")
	@GetMapping("/{matchId}")
	public ResponseEntity<ResponseDto<MatchPredictionResponse>> findOne(
		@PathVariable("matchId")
		@Parameter(description = "경기 ID")
		Long matchId) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"경기예측 현황 조회 성공",
			matchPredictionReadService.findOne(matchId))
		);
	}

	@Operation(summary = "경기예측 저장 API", description = "경기예측을 저장한다.")
	@PatchMapping
	public ResponseEntity<ResponseDto<Long>> update(@RequestBody MatchPredictionRequest matchPredictionRequest) {
		return ResponseEntity.ok(new ResponseDto<>(
				SUCCESS.name(),
				"경기예측 저장 성공",
				matchPredictionService.update(matchPredictionRequest.toServiceRequest())
			)
		);
	}

}
