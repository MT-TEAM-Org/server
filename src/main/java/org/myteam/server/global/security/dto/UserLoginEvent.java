package org.myteam.server.global.security.dto;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class UserLoginEvent extends ApplicationEvent {
    private final UUID publicId;
    private String ip;

    public UserLoginEvent(Object source, UUID publicId,String ip) {
        super(source);
        this.publicId = publicId;
        this.ip=ip;
    }
}
