package org.myteam.server.admin.dto.ctes;

import com.blazebit.persistence.CTE;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.UUID;


@Entity
@Getter
@CTE
public class BoardCountCte {
    @Id
    private UUID publicId;
    private Long count;
    private Integer recommendCount;
}
