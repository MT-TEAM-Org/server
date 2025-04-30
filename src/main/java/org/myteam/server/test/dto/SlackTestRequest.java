package org.myteam.server.test.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlackTestRequest {

    private String name;
    private String email;
    private String message;
}
