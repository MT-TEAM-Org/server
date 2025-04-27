package org.myteam.server.match.matchPredictionMember.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myteam.server.IntegrationTestSupport;
import org.myteam.server.TestContainerSupport;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.match.match.domain.Match;
import org.myteam.server.match.match.domain.MatchCategory;
import org.myteam.server.match.matchPrediction.domain.MatchPrediction;
import org.myteam.server.match.matchPredictionMember.domain.MatchPredictionMember;
import org.myteam.server.match.team.domain.Team;
import org.myteam.server.match.team.domain.TeamCategory;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;

public class MatchPredictionMemberReadServiceTest extends IntegrationTestSupport {

    @Autowired
    private MatchPredictionMemberReadService matchPredictionMemberReadService;

    @DisplayName("이미 좋아요를 누른 뉴스면 예외가 발생한다.")
    @Test
    void confirmExistMemberTest() {
        Team team1 = createTeam(1, TeamCategory.FOOTBALL);
        Team team2 = createTeam(2, TeamCategory.FOOTBALL);

        Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
        MatchPrediction matchPrediction = createMatchPrediction(match, 1, 2);

        Member member = Member.builder()
                .email("test" + 1 + "@test.com")
                .password("1234")
                .tel("12345")
                .nickname("test")
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .publicId(UUID.randomUUID())
                .status(MemberStatus.ACTIVE)
                .build();
        memberJpaRepository.save(member);

        matchPredictionMemberRepository.save(
                MatchPredictionMember.builder()
                        .matchPrediction(matchPrediction)
                        .member(member)
                        .build());

        assertThatThrownBy(() -> matchPredictionMemberReadService.confirmExistMember(matchPrediction.getId(),
                member.getPublicId()))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.ALREADY_MEMBER_MATCH_PREDICTION.getMsg());

    }

    @Test
    @DisplayName("1. confirmExistMember - 이미 예측한 경우 예외 발생")
    void confirmExistMember_alreadyPredicted() {
        // given
        Member member = createMember(1);
        Team team1 = createTeam(1, TeamCategory.FOOTBALL);
        Team team2 = createTeam(2, TeamCategory.FOOTBALL);

        Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
        MatchPrediction matchPrediction = createMatchPrediction(match, 1, 2);
        MatchPredictionMember matchMember = createMatchPredictionMember(member, matchPrediction);

        // when & then
        assertThatThrownBy(() -> matchPredictionMemberReadService.confirmExistMember(
                matchMember.getMatchPrediction().getId(), matchMember.getMember().getPublicId()))
                .isInstanceOf(PlayHiveException.class)
                .hasMessage(ErrorCode.ALREADY_MEMBER_MATCH_PREDICTION.getMsg());
    }

    @Test
    @DisplayName("2. confirmExistMember - 예측 기록 없으면 통과")
    void confirmExistMember_noPrediction() {
        // given
        UUID memberId = UUID.randomUUID();

        // when & then
        assertThatCode(() -> matchPredictionMemberReadService.confirmExistMember(1L, memberId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("3. confirmPredictionMember - 예측 기록 존재시 반환")
    void confirmPredictionMember_exist() {
        // given
        UUID memberId = UUID.randomUUID();
        Member member = createMember(1);
        Team team1 = createTeam(1, TeamCategory.FOOTBALL);
        Team team2 = createTeam(2, TeamCategory.FOOTBALL);

        Match match = createMatch(team1, team2, MatchCategory.FOOTBALL, LocalDate.now().atStartOfDay());
        MatchPrediction matchPrediction = createMatchPrediction(match, 1, 2);
        MatchPredictionMember matchMember = createMatchPredictionMember(member, matchPrediction);

        // when
        MatchPredictionMember found = matchPredictionMemberReadService.confirmPredictionMember(
                matchMember.getMatchPrediction().getId(), matchMember.getMember().getPublicId());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getMember().getPublicId()).isEqualTo(member.getPublicId());
    }

    @Test
    @DisplayName("4. confirmPredictionMember - 예측 기록 없으면 null 반환")
    void confirmPredictionMember_notExist() {
        // given
        UUID memberId = UUID.randomUUID();

        // when
        MatchPredictionMember found = matchPredictionMemberReadService.confirmPredictionMember(1L, memberId);

        // then
        assertThat(found).isNull();
    }

}
