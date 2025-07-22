package org.myteam.server.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberUpdateRequest {
    @Pattern(regexp = "^[0-9a-zA-Z_]+@[0-9a-zA-Z]+(\\.[a-zA-Z]{2,3}){1,2}$", message = "이메일 형식으로 작성해주세요")
    private String email; // 계정

    @NotNull(message = "영문 + 숫자 조합 4 ~ 10자 이내로 작성해주세요")
    @Size(min = 4, max = 10, message = "비밀번호는 4~10자 이내로 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{4,10}$",
            message = "비밀번호는 영문 + 숫자 조합 4 ~ 10자 이내로 작성해주세요."
    )
    private String password;

    @NotBlank
    @Pattern(regexp = "^010[0-9]{7,8}$", message = "연락처는 '010'으로 시작하고 뒤에 7 또는 8 자리 숫자로 작성해주세요.")
    private String tel; // 전화번호

    // _-. 를 포함하는 닉네임 생성 가능
    @Pattern(regexp = "^[a-zA-Z가-힣0-9]{1,10}$", message = "한글/영문/_- 1~10자 이내로 작성해주세요")
    private String nickname;
}
