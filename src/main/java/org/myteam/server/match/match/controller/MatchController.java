package org.myteam.server.match.match.controller;

import static org.myteam.server.global.web.response.ResponseStatus.*;

import java.util.List;

import org.myteam.server.global.web.response.ResponseDto;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.match.dto.service.response.MatchEsportsScheduleResponse;
import org.myteam.server.match.match.dto.service.response.MatchEsportsYoutubeResponse;
import org.myteam.server.match.match.dto.service.response.MatchResponse;
import org.myteam.server.match.match.dto.service.response.MatchScheduleListResponse;
import org.myteam.server.match.match.service.MatchReadService;
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
@RequestMapping("/api/match")
public class MatchController {

	private final MatchReadService matchReadService;

	@Operation(summary = "경기 일정 조회 API", description = "당일과 다음날 새벽6시까지 경기 일정을 조회한다")
	@GetMapping("/schedule/{matchCategory}")
	public ResponseEntity<ResponseDto<MatchScheduleListResponse>> findSchedulesBetweenDate(
		@PathVariable("matchCategory")
		@Parameter(description = "경기 유형 FOOTBALL, BASEBALL")
		MatchCategory matchCategory) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"경기 일정 조회 성공",
			matchReadService.findSchedulesBetweenDate(matchCategory))
		);
	}

	@Operation(summary = "ESPORTS 경기 일정 조회 API", description = "당일과 다음날 새벽6시까지 ESPORTS 경기 일정을 조회한다")
	@GetMapping("/esports/schedule")
	public ResponseEntity<ResponseDto<List<MatchEsportsScheduleResponse>>> findEsportsSchedulesBetweenDate() {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"경기 일정 조회 성공",
			matchReadService.findEsportsSchedulesBetweenDate())
		);
	}

	@Operation(summary = "경기 상세 조회 API", description = "경기를 상세 조회한다.")
	@GetMapping("/{matchId}")
	public ResponseEntity<ResponseDto<MatchResponse>> findOne(
		@PathVariable("matchId")
		@Parameter(description = "경기 ID")
		Long matchId) {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"경기 상세 조회 성공",
			matchReadService.findOne(matchId))
		);
	}

	@Operation(summary = "E스포츠의 유튜브 라이브 여부를 조회 API", description = "E스포츠의 유튜브 라이브 여부를 조회한다.")
	@GetMapping("/esports/youtube")
	public ResponseEntity<ResponseDto<MatchEsportsYoutubeResponse>> confirmEsportsYoutube() {
		return ResponseEntity.ok(new ResponseDto<>(
			SUCCESS.name(),
			"E스포츠의 유튜브 라이브 여부를 조회 성공",
			matchReadService.confirmEsportsYoutube())
		);
	}

}
