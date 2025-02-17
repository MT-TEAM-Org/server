package org.myteam.server.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.domain.Base;
import org.myteam.server.global.exception.ErrorCode;
import org.myteam.server.global.exception.PlayHiveException;
import org.myteam.server.member.domain.GenderType;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberStatus;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.myteam.server.member.dto.PasswordChangeRequest;
import org.myteam.server.profile.dto.request.ProfileRequestDto.MemberUpdateRequest;
import org.myteam.server.util.AESCryptoUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.UUID;

import static org.myteam.server.member.domain.MemberRole.ADMIN;
import static org.myteam.server.member.domain.MemberRole.USER;
import static org.myteam.server.member.domain.MemberStatus.PENDING;
import static org.myteam.server.member.domain.MemberType.LOCAL;

@Slf4j
@Entity
@Getter
@Table(name = "p_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends Base {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "public_id", nullable = false, updatable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID publicId = UUID.randomUUID();

    @Column(nullable = false)
    private String email; // 계정

    @Column(nullable = false)
    private String encodedPassword;

    @Column(length = 11)
    private String tel;

    @Column(length = 60)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role = USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberType type = LOCAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = PENDING;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private MemberActivity memberActivity;

    @Enumerated(EnumType.STRING)
    private GenderType genderType;

    private int birthYear;
    private int birthMonth;
    private int birthDay;


    @Builder
    public Member(String email, String encodedPassword, String tel, String nickname, MemberRole role, MemberType type, UUID publicId, MemberStatus status) {
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.tel = tel;
        this.nickname = nickname;
        this.role = role;
        this.type = type;
        this.publicId = publicId;
        this.status = status;
    }

    @Builder
    public Member(String email, String encodedPassword, String tel, String nickname) {
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.tel = tel;
        this.nickname = nickname;
    }

    // 전체 업데이트 메서드
    public void update(String encodedPassword, String tel, String nickname) {
        this.encodedPassword = encodedPassword;
        this.tel = tel;
        this.nickname = nickname;
    }

    public void updatePassword(String encodedPassword) {
        this.encodedPassword = encodedPassword; // 비밀번호 변경 시 암호화 필요
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateStatus(MemberStatus memberStatus) {
        this.status = memberStatus;
    }

    public void updateType(MemberRole role) {
        this.role = role;
    }

    public boolean verifyOwnEmail(String email) {
        return email.equals(this.email);
    }

    public boolean isAdmin() {
        return this.role.equals(ADMIN);
    }

    public boolean validatePassword(String inputPassword, AESCryptoUtil aesCryptoUtil) {
        try {
            // ✅ 저장된 암호화된 비밀번호를 AES 복호화
            String decryptedPassword = aesCryptoUtil.findOriginPwd(this.encodedPassword);

            // ✅ 평문 패스워드와 복호화된 패스워드 비교
            boolean isValid = inputPassword.equals(decryptedPassword);

            log.info("Input password: {}", inputPassword);
            log.info("Decrypted password: {}", decryptedPassword);
            log.info("Is valid: {}", isValid);

            return isValid;
        } catch (Exception e) {
            log.error("Failed to validate password", e);
            return false;
        }
    }

    public void confirmMemberEquals(Member member) {
        if(!Objects.equals(this.publicId, member.getPublicId())) {
            throw new PlayHiveException(ErrorCode.MEMBER_NOT_EQUALS);
        }
    }

    public void updateMemberActivity(MemberActivity memberActivity) {
        this.memberActivity = memberActivity;
    }

    public void updateGender(GenderType genderType) {
        this.genderType = genderType;
    }

    public void updateBirthDate(String birthDate) {
        this.birthYear = Integer.parseInt(birthDate.substring(0, 2));
        this.birthMonth = Integer.parseInt(birthDate.substring(2, 4));
        this.birthDay = Integer.parseInt(birthDate.substring(4));
    }
}
