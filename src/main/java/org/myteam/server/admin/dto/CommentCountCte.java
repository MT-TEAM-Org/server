package org.myteam.server.admin.dto;


import com.blazebit.persistence.CTE;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
@CTE
public class CommentCountCte {
    @Id
    private UUID publicID;
    private Long count;
    private Integer recommendCount;
}
