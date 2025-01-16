package org.myteam.server.member.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExistMemberNicknameRequest {
    // _-. 를 포함하는 닉네임 생성 가능
    @Pattern(regexp = "^[a-zA-Z가-힣0-9]{1,10}$", message = "한글/영문/_- 1~10자 이내로 작성해주세요")
    private String nickname;
}
