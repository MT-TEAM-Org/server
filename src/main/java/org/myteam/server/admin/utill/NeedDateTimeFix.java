package org.myteam.server.admin.utill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class NeedDateTimeFix {

    @Schema(example = "2025.06.06")
    private String createDate;
    public NeedDateTimeFix(String createDate) {
        this.createDate = createDate;
    }
    public void updateCreateDate(String date) {
        this.createDate = date;
    }

}
