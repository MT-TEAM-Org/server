package org.myteam.server.match.matchSchedule.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;

import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.match.matchSchedule.domain.MatchCategory;
import org.myteam.server.match.matchSchedule.dto.service.response.MatchScheduleListResponse;
import org.myteam.server.match.matchSchedule.service.MatchScheduleReadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/match/schedule")
public class MatchScheduleController {

	private final MatchScheduleReadService matchScheduleReadService;

	@Operation(summary = "경기 일정 조회 API", description = "당일과 다음날 새벽6시까지 경기 일정을 조회한다")
	@GetMapping("/{matchCategory}")
	public ResponseEntity<ResponseDto<MatchScheduleListResponse>> findSchedulesBetweenDate(
		@PathVariable("matchCategory")
		@Parameter(description = "경기 유형 FOOTBALL, BASEBALL, ESPORTS")
		MatchCategory matchCategory) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"경기 일정 조회 성공",
			matchScheduleReadService.findSchedulesBetweenDate(matchCategory))
		);
	}

}
