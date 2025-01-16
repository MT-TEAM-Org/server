package org.myteam.server.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.global.domain.Base;
import org.myteam.server.member.domain.GenderType;
import org.myteam.server.member.domain.MemberRole;
import org.myteam.server.member.domain.MemberType;
import org.myteam.server.member.dto.MemberSaveRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;

import static org.myteam.server.member.domain.MemberRole.USER;
import static org.myteam.server.member.domain.MemberType.LOCAL;

@Slf4j
@Entity
@Getter
@Table(name = "p_oauth2_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Oauth2Member extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email; // 계정

    @Column(nullable = false, length = 60) // 패스워드 인코딩(BCrypt)
    private String password; // 비밀번호

    @Column(length = 11)
    private String tel;

    @Column(length = 60)
    private String name;

    @Column(length = 60)
    private String nickname;

    // YYYY-MM-dd 형식
    @Column(name = "birth_date")
    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private GenderType gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRole role = USER;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MemberType type = LOCAL;

    @Builder
    public Oauth2Member(Long id, String email, String password, String tel, String name, String nickname, LocalDate birthdate, GenderType gender, MemberRole role, MemberType type, UUID publicId) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.tel = tel;
        this.name = name;
        this.nickname = nickname;
        this.birthdate = birthdate;
        this.gender = gender;
        this.role = role;
        this.type = type;
    }

    @Builder
    public Oauth2Member(MemberSaveRequest memberSaveRequest, PasswordEncoder passwordEncoder) {
        this.email = memberSaveRequest.getEmail();
        this.password = passwordEncoder.encode(memberSaveRequest.getPassword());
        this.tel = memberSaveRequest.getTel();
        this.nickname = memberSaveRequest.getNickname();
    }
}