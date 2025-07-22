package org.myteam.server.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.member.domain.MemberStatus;

@Getter
@NoArgsConstructor
public class MemberStatusUpdateRequest {
    @Pattern(regexp = "^[0-9a-zA-Z]+@[0-9a-zA-Z]+(\\.[a-zA-Z]{2,3}){1,2}$", message = "이메일 형식으로 작성해주세요")
    private String email; // 계정

    @NotNull
    private MemberStatus status;

    @Schema(description = "관리자가 회원의 상태를 업데이트 할때 따로 넣을게 없다면은 그냥 null로 해주세요")
    private String content;


    @Builder
    public static MemberStatusUpdateRequest memberStatusUpdateRequestBuilder(String email
            , MemberStatus status, String content) {
        return MemberStatusUpdateRequest
                .builder()
                .email(email)
                .status(status)
                .content(content)
                .build();
    }

}
