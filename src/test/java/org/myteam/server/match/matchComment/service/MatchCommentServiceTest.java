package org.myteam.server.match.matchComment.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.matchComment.domain.MatchComment;
import org.myteam.server.match.matchComment.dto.service.request.MatchCommentSaveServiceRequest;
import org.myteam.server.match.matchComment.dto.service.request.MatchCommentUpdateServiceRequest;
import org.myteam.server.match.matchComment.dto.service.response.MatchCommentResponse;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.myteam.server.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class MatchCommentServiceTest extends IntegrationTestSupport {

	@Autowired
	private MatchCommentService matchCommentService;

	@DisplayName("경기댓글을 저장한다.")
	@Test
	@Transactional
	void saveTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);

		Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());

		Member member = createMember(1);

		MatchCommentSaveServiceRequest matchCommentSaveServiceRequest = MatchCommentSaveServiceRequest.builder()
			.matchId(match.getId())
			.comment("댓글 테스트")
			.ip("1.1.1.1")
			.imgUrl("www.test.com")
			.build();

		MatchCommentResponse matchCommentResponse = matchCommentService.save(matchCommentSaveServiceRequest);

		assertThat(matchCommentRepository.findById(matchCommentResponse.getMatchCommentId()).get())
			.extracting("id", "match.id", "member.publicId", "comment", "ip", "imgUrl")
			.contains(matchCommentResponse.getMatchCommentId(), match.getId(), member.getPublicId(), "댓글 테스트", "1.1.1.1", "www.test.com");

		// assertAll(
		// 	() -> assertThat(matchCommentRepository.findById(matchCommentResponse.getMatchCommentId()).get())
		// 		.extracting("id", "match.id", "member.publicId", "comment", "ip")
		// 		.contains(matchCommentResponse.getMatchCommentId(), match.getId(), member.getPublicId(), "댓글 테스트", "1.1.1.1");
		// 	() -> assertThat(newsCountRepository.findByNewsId(match.getId()).get().getCommentCount()).isEqualTo(11)
		// );
	}

	@DisplayName("경기댓글을 수정한다.")
	@Test
	void updateTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);

		Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());

		Member member = createMember(1);

		MatchComment matchComment = createMatchComment(match, member, "경기 댓글 테스트");

		MatchCommentUpdateServiceRequest matchCommentUpdateServiceRequest = MatchCommentUpdateServiceRequest.builder()
			.matchCommentId(matchComment.getId())
			.comment("경기 댓글 수정 테스트")
			.imgUrl("www.modifyTest.com")
			.build();

		Long updatedCommentId = matchCommentService.update(matchCommentUpdateServiceRequest);

		assertThat(matchCommentRepository.findById(updatedCommentId).get())
			.extracting("id", "match.id", "member.publicId", "comment", "ip", "imgUrl")
			.contains(matchComment.getId(), match.getId(), member.getPublicId(), "경기 댓글 수정 테스트", "1.1.1.1", "www.modifyTest.com");
	}

	@DisplayName("경기댓글을 삭제한다.")
	@Test
	@Transactional
	void deleteTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);

		Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());

		Member member = createMember(1);

		MatchComment matchComment = createMatchComment(match, member, "경기 댓글 테스트");

		Long deletedCommentId = matchCommentService.delete(matchComment.getId());

		assertThatThrownBy(() -> matchCommentService.delete(deletedCommentId))
			.isInstanceOf(PlayHiveException.class)
			.hasMessage(ErrorCode.MATCH_COMMENT_NOT_FOUND.getMsg());

		// assertAll(
		// 	() -> assertThatThrownBy(() -> newsCommentService.delete(deletedCommentId))
		// 		.isInstanceOf(PlayHiveException.class)
		// 		.hasMessage(ErrorCode.NEWS_COMMENT_NOT_FOUND.getMsg()),
		// 	() -> assertThat(newsCountRepository.findByNewsId(news.getId()).get().getCommentCount()).isEqualTo(9)
		// );
	}
}
