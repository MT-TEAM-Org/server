package org.myteam.server.member.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import org.myteam.server.member.domain.MemberStatus;

@Getter
@Builder
public class MemberStatusUpdateRequest {
    @Pattern(regexp = "^[0-9a-zA-Z]+@[0-9a-zA-Z]+(\\.[a-zA-Z]{2,3}){1,2}$", message = "이메일 형식으로 작성해주세요")
    private String email; // 계정

    @NotNull
    private MemberStatus status;

    public static MemberStatusUpdateRequest memberStatusUpdateRequestBuilder(String email
            ,MemberStatus memberStatus){
        return MemberStatusUpdateRequest
                .builder()
                .email(email)
                .status(memberStatus)
                .build();
    }
}
