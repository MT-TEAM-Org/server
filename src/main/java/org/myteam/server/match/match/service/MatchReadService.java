package org.myteam.server.match.match.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.match.repository.MatchQueryRepository;
import org.myteam.server.match.match.repository.MatchRepository;
import org.myteam.server.match.match.dto.service.response.MatchResponse;
import org.myteam.server.match.match.dto.service.response.MatchScheduleListResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchReadService {

	private final MatchQueryRepository matchQueryRepository;
	private final MatchRepository matchRepository;

	public Match findById(Long id) {
		return matchRepository.findById(id)
			.orElseThrow(() -> new PlayHiveException(ErrorCode.MATCH_NOT_FOUNT));
	}

	public MatchScheduleListResponse findSchedulesBetweenDate(MatchCategory matchCategory) {
		LocalDate today = LocalDate.now();
		LocalDateTime startOfDay = today.atStartOfDay();
		LocalDateTime endTime = today.plusDays(1).atTime(LocalTime.of(6, 0));

		return MatchScheduleListResponse.createResponse(
			matchQueryRepository.findSchedulesBetweenDate(startOfDay, endTime, matchCategory)
				.stream()
				.map(MatchResponse::createResponse)
				.toList());
	}

	public MatchResponse findOne(Long id) {
		return MatchResponse.createResponse(findById(id));
	}

}
