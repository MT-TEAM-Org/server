package org.myteam.server.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.util.date.DateFormatUtil;
import org.myteam.server.member.domain.GenderType;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public record MemberSearchRequestDto() {

    @Getter
    @NoArgsConstructor
    @Schema(name = "RequestMemberSearch", description = "조건에 따른 회원들의 정보를 가져올떄 사용합니다")
    public static class RequestMemberSearch {
        private String nickName;
        private String email;
        @Size(max = 11)
        private String tel;
        private GenderType genderType;
        @Schema(description = "회원의 가입경로 구글,디스코드 등등을 말합니다.")
        private MemberType memberType;
        private MemberStatus status;
        @Schema(description = "회원의 생년월일 중 년에 해당됩니다.")
        private int birthYear;
        @Schema(description = "회원의 생년월일 중 월에 해당됩니다.")
        private int birthMonth;
        @Schema(description = "회원의 생년월일 중 일에 해당됩니다.")
        private int birthDay;
        private String signInStart;
        private String signInEnd;
        @NotNull
        private int offset;

        @Builder
        public RequestMemberSearch(String nickName, String email, String tel
                , GenderType genderType, MemberType memberType, int birthYear,
                                   int birthMonth, int birthDay, String signInStart, String signInEnd, int offset) {
            this.nickName = nickName;
            this.email = email;
            this.tel = tel;
            this.genderType = genderType;
            this.memberType = memberType;
            this.birthYear = birthYear;
            this.birthMonth = birthMonth;
            this.birthDay = birthDay;
            this.signInStart = signInStart;
            this.signInEnd = signInEnd;
            this.offset = offset;
        }

        public List<Integer> provideBirthDate() {
            List<Integer> birthDate = new ArrayList<>();
            birthDate.add(birthYear);    // 이건 null 허용
            birthDate.add(birthMonth);
            birthDate.add(birthDay);

            return birthDate;
        }

        public int getOffset() {
            return this.offset - 1;
        }

        public LocalDateTime provideStartTime() {
            if (signInStart == null) {
                return null;
            }
            LocalDate localDate = LocalDate.parse(signInStart, DateFormatUtil.formatByDot);
            return localDate.atStartOfDay();
        }

        public LocalDateTime provideEndTime() {
            if (signInEnd == null) {
                return null;
            }
            LocalDate localDate = LocalDate.parse(signInEnd, DateFormatUtil.formatByDot);
            return localDate.atStartOfDay();
        }


    }

    @Getter
    @NoArgsConstructor
    @Schema(name = "RequestMemberDetail", description = "회원의 상세정보를 요구시에 사용합니다 혹은" +
            "회원을 대상으로한 신고 목록을 조회시에 사용하면 됩니다.")
    public static class RequestMemberDetail {
        @NotNull
        private UUID publicId;

        @Schema(description = "이 객체는 회원 한명에 대한 데이터와 회원에 대해 발생한 신고에대해서" +
                "쓰이기에 offset을 넣주었고 회원 한명에대한 데이터를 볼때에는 offset에 아무값이나 넣어줘도" +
                "알아서 걸러줍니다.")
        @NotNull
        private int offset;

        @Builder
        public RequestMemberDetail(UUID publicId, int offset) {
            this.publicId = publicId;
            this.offset = offset;

        }

        public int getOffset() {
            return this.offset - 1;
        }
    }


}
