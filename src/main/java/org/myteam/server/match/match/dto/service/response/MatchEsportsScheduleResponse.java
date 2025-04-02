package org.myteam.server.match.match.dto.service.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.myteam.server.match.match.domain.Match;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchEsportsScheduleResponse {
	private Long id;
	private String startTime;
	private List<MatchResponse> list;

	public MatchEsportsScheduleResponse(List<Match> matches) {
		this.id = matches.get(matches.size() - 1).getId(); // 마지막 경기 ID를 기준으로 설정
		this.startTime = matches.get(0).getStartTime().toString(); // 첫번째 경기 시간을 기준
		this.list = matches.stream().map(MatchResponse::createResponse).toList();
	}

	public static String formatLocalDateTimeToYearMonth(LocalDateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return dateTime.format(formatter);
	}
}
