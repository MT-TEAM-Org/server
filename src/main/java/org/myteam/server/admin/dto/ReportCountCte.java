package org.myteam.server.admin.dto;

import com.blazebit.persistence.CTE;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@CTE
@NoArgsConstructor
public class ReportCountCte {
    @Id
    private UUID publicId;
    private Long count;
}
