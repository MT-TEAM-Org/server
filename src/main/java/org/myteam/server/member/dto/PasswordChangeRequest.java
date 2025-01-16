package org.myteam.server.member.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordChangeRequest {
    @NotNull
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{4,10}$", message = "비밀번호는 영문, 숫자 조합 4 ~ 10자 이내로 입력해주세요")
    private String password;

    @NotNull
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{4,10}$", message = "비밀번호는 영문, 숫자 조합 4 ~ 10자 이내로 입력해주세요")
    private String newPassword;

    @NotNull
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{4,10}$", message = "비밀번호는 영문, 숫자 조합 4 ~ 10자 이내로 입력해주세요")
    private String confirmPassword;

    @Builder
    public PasswordChangeRequest(String password, String newPassword, String confirmPassword) {
        this.password = password;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public boolean checkPasswordAndConfirmPassword() {
        return newPassword.equals(confirmPassword);
    }
}
