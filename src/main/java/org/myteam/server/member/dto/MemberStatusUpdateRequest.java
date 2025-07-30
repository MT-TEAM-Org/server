package org.myteam.server.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.member.domain.MemberStatus;

@Getter
@NoArgsConstructor
public class MemberStatusUpdateRequest {
    @Pattern(regexp = "^[0-9a-zA-Z_]+@[0-9a-zA-Z]+(\\.[a-zA-Z]{2,3}){1,2}$", message = "이메일 형식으로 작성해주세요")
    private String email; // 계정

    @NotNull(message = "memberstatus는 필수값입니다.")
    @Schema(description = "필수값입니다. 같다면 이전과 같은값을 다르다면 바뀐값을 넣어주세요.")
    private MemberStatus status;

    @Schema(description = "관리자가 회원의 상태를 업데이트 할때 따로 넣을게 없다면은 그냥 null로 해주세요")
    private String content;

    @Builder
    public MemberStatusUpdateRequest (String email
            , MemberStatus status, String content) {
        this.status=status;
        this.email=email;
        this.content=content;
    };
}
