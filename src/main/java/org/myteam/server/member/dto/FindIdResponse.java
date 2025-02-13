package org.myteam.server.member.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.myteam.server.member.domain.MemberType;

@Builder
@Getter
@AllArgsConstructor
public class FindIdResponse {
    private String email;
    private MemberType type;

    public static FindIdResponse createResponse(String email, MemberType type) {
        return FindIdResponse.builder()
                .email(email)
                .type(type)
                .build();
    }
}
