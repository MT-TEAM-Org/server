package org.myteam.server.common.certification.mail.domain;

public enum EmailType {
    CERTIFICATION, // 인증 코드 메일
    TEMPORARY_PASSWORD, // 임시 비밀번호 메일
    WELCOME, // 회원가입

    NOTIFY_ADMIN_SUSPEND, //관리자 정지 알림

    NOTIFY_ADMIN_SUSPEND_GLOBAL //관리자 계정 정지 알림 전체
}
