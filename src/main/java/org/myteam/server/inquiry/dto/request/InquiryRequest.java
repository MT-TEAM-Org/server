package org.myteam.server.inquiry.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class InquiryRequest {
    @NotNull(message = "문의 내용이 없으면 안됩니다.")
    private String content;
    private UUID memberPublicId;
}
