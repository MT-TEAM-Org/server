package org.myteam.server.admin.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminUpdateDto {

    @Size(max = 10)
    private String nickName;
    @Size(min = 4, max = 10)
    private String password;
    private String imgUrl;
}
