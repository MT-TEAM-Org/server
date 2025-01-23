package org.myteam.server.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDeleteRequest {
    //    @NotBlank
    @Pattern(regexp = "^[0-9a-zA-Z]+@[0-9a-zA-Z]+(\\.[a-zA-Z]{2,3}){1,2}$", message = "이메일 형식으로 작성해주세요")
    private String email; // 계정

    @NotBlank
    // @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "비밀번호는 영문+숫자 조합 4 ~ 10자 이내로 입력해주세요")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{4,10}$", message = "비밀번호는 영문, 숫자 조합 4 ~ 10자 이내로 입력해주세요")
    private String password; // 비밀번호
}
