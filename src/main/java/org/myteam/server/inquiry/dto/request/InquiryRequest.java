package org.myteam.server.inquiry.dto.request;

<<<<<<< HEAD
import jakarta.validation.constraints.NotNull;
=======
>>>>>>> 624ff52 (feat: 문의하기 기능 추가)
import lombok.Getter;

import java.util.UUID;

@Getter
public class InquiryRequest {
<<<<<<< HEAD
    @NotNull(message = "문의 내용이 없으면 안됩니다.")
=======
>>>>>>> 624ff52 (feat: 문의하기 기능 추가)
    private String content;
    private UUID memberPublicId;
}
