package org.myteam.server.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

@NoArgsConstructor
@Getter
public class SignupRequestDto {

    @NotBlank(message = "username은 필수 입력 값입니다.")
    private String username;

    @NotBlank(message = "name은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "password는 필수 입력 값입니다.")
    private String password;

    @NotBlank(message = "email은 필수 입력 값입니다.")
    private String email;

    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .username(username)
                .name(name)
                .password(passwordEncoder.encode(password))
                .email(email)
                .role(MemberRole.USER)
                .type(MemberType.LOCAL)
                .build();
    }
}
