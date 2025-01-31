package org.myteam.server.inquiry.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class InquiryRequest {
    private String content;
    private UUID memberPublicId;
}
