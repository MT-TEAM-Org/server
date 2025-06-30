package org.myteam.server.global.security.dto;


import lombok.Getter;

@Getter
public class AdminBanEvent {
    private String email;


    public AdminBanEvent(String email) {
        this.email = email;
    }
}
