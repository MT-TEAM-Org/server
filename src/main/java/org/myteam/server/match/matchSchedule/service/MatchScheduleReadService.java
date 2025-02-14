package org.myteam.server.match.matchSchedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.myteam.server.match.matchSchedule.domain.MatchCategory;
import org.myteam.server.match.matchSchedule.dto.service.response.MatchScheduleListResponse;
import org.myteam.server.match.matchSchedule.dto.service.response.MatchScheduleResponse;
import org.myteam.server.match.matchSchedule.repository.MatchScheduleQueryRepository;
import org.myteam.server.match.matchSchedule.repository.MatchScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchScheduleReadService {

	private final MatchScheduleQueryRepository matchScheduleQueryRepository;

	public MatchScheduleListResponse findSchedulesBetweenDate(MatchCategory matchCategory) {
		LocalDate today = LocalDate.now();
		LocalDateTime startOfDay = today.atStartOfDay();
		LocalDateTime endTime = today.plusDays(1).atTime(LocalTime.of(6, 0));

		return MatchScheduleListResponse.createResponse(
			matchScheduleQueryRepository.findSchedulesBetweenDate(startOfDay, endTime, matchCategory)
				.stream()
				.map(MatchScheduleResponse::createResponse)
				.toList());
	}

}
