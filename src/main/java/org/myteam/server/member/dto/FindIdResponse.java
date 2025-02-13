package org.myteam.server.member.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor
public class FindIdResponse {
    private List<MemberInfo> members;

    public static FindIdResponse createResponse(List<Member> memberList) {
        List<MemberInfo> memberInfos = memberList.stream()
                .map(member -> new MemberInfo(member.getEmail(), member.getType()))
                .collect(Collectors.toList());
        return new FindIdResponse(memberInfos);
    }

    @Getter
    @AllArgsConstructor
    public static class MemberInfo {
        private String email;
        private MemberType type;
    }
}
