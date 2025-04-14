package org.myteam.server.match.matchPredictionMember.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

public class MatchPredictionMemberReadServiceTest extends TestContainerSupport {

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

}
