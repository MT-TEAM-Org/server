package org.myteam.server.mypage.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.myteam.server.board.domain.BoardOrderType;
import org.myteam.server.board.domain.BoardSearchType;
import org.myteam.server.global.page.request.PageInfoRequest;
import org.myteam.server.global.page.request.PageInfoServiceRequest;
import org.myteam.server.member.domain.GenderType;
import org.myteam.server.member.domain.MemberRole;

public record MyPageRequest() {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardRequest extends PageInfoServiceRequest {
        private BoardOrderType orderType;
        private BoardSearchType searchType;
        private String search;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageUpdateRequest {
        private String email;
        private String password;
        private String tel;
        private String nickname;
        private GenderType genderType;
        private String birthDate;
        private String imageUrl;
    }

}
