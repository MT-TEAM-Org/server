package org.myteam.server.global.security.dto;


import lombok.Getter;

@Getter
public class AdminBanEvent {
    private String email;
    private String ip;

    public AdminBanEvent(String email,String ip) {
        this.email = email;
        this.ip=ip;
    }
}
