package org.myteam.server.admin.dto;

import com.blazebit.persistence.CTE;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@CTE
public class BoardCountCte {
    @Id
    private UUID publicId;
    private Long count;
    private Integer recommendCount;
}
