package org.myteam.server.match.matchReply.service;

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
import org.myteam.server.match.matchReply.domain.MatchReply;
import org.myteam.server.match.matchReply.dto.service.request.MatchReplySaveServiceRequest;
import org.myteam.server.match.matchReply.dto.service.request.MatchReplyServiceRequest;
import org.myteam.server.match.matchReply.dto.service.request.MatchReplyUpdateServiceRequest;
import org.myteam.server.match.matchReply.dto.service.response.MatchReplyResponse;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.myteam.server.member.entity.Member;
import org.myteam.server.news.news.domain.News;
import org.myteam.server.news.news.domain.NewsCategory;
import org.myteam.server.news.newsComment.domain.NewsComment;
import org.myteam.server.news.newsReply.domain.NewsReply;
import org.myteam.server.news.newsReply.dto.service.request.NewsReplySaveServiceRequest;
import org.myteam.server.news.newsReply.dto.service.request.NewsReplyUpdateServiceRequest;
import org.myteam.server.news.newsReply.dto.service.response.NewsReplyResponse;
import org.myteam.server.news.newsReply.service.NewsReplyService;
import org.springframework.beans.factory.annotation.Autowired;

public class NewsReplyServiceTest extends IntegrationTestSupport {

	@Autowired
	private MatchReplyService matchReplyService;

	@DisplayName("경기 대댓글을 저장한다.")
	@Test
	void saveTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);

		Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());

		Member member = createMember(1);

		MatchComment matchComment = createMatchComment(match, member, "경기 댓글 테스트");

		MatchReplySaveServiceRequest matchReplySaveServiceRequest = MatchReplySaveServiceRequest.builder()
			.matchCommentId(matchComment.getId())
			.comment("대댓글 테스트")
			.ip("1.1.1.1")
			.imgUrl("wwww.test.com")
			.build();

		MatchReplyResponse matchReplyResponse = matchReplyService.save(matchReplySaveServiceRequest);

		assertThat(matchReplyRepository.findById(matchReplyResponse.getMatchReplyId()).get())
			.extracting("id", "matchComment.id", "member.publicId", "comment", "ip", "imgUrl")
			.contains(matchReplyResponse.getMatchReplyId(), matchComment.getId(), member.getPublicId(), "대댓글 테스트",
				"1.1.1.1", "wwww.test.com");
	}

	@DisplayName("경기 대댓글을 수정한다.")
	@Test
	void updateTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);

		Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());

		Member member = createMember(1);

		MatchComment matchComment = createMatchComment(match, member, "경기 댓글 테스트");

		MatchReply matchReply = createMatchReply(matchComment, member, "경기 대댓글 테스트");

		MatchReplyUpdateServiceRequest matchReplyUpdateServiceRequest = MatchReplyUpdateServiceRequest.builder()
			.matchReplyId(matchReply.getId())
			.comment("경기 대댓글 수정 테스트")
			.imgUrl("www.modifyTest.com")
			.build();

		Long updatedReplyId = matchReplyService.update(matchReplyUpdateServiceRequest);

		assertThat(matchReplyRepository.findById(updatedReplyId).get())
			.extracting("id", "matchComment.id", "member.publicId", "comment", "ip", "imgUrl")
			.contains(matchReply.getId(), matchComment.getId(), member.getPublicId(), "경기 대댓글 수정 테스트", "1.1.1.1", "www.modifyTest.com");
	}

	@DisplayName("경기 대댓글을 삭제한다.")
	@Test
	void deleteTest() {
		Team team1 = createTeam(1, TeamCategory.FOOTBALL);
		Team team2 = createTeam(2, TeamCategory.FOOTBALL);

		Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());

		Member member = createMember(1);

		MatchComment matchComment = createMatchComment(match, member, "경기 댓글 테스트");

		MatchReply matchReply = createMatchReply(matchComment, member, "경기 대댓글 테스트");

		Long deletedReplyId = matchReplyService.delete(matchReply.getId());

		assertThatThrownBy(() -> matchReplyService.delete(deletedReplyId))
			.isInstanceOf(PlayHiveException.class)
			.hasMessage(ErrorCode.MATCH_REPLY_NOT_FOUND.getMsg());
	}

}
