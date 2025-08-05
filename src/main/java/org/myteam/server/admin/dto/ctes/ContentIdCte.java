package org.myteam.server.admin.dto.ctes;


import com.blazebit.persistence.CTE;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@CTE
public class ContentIdCte {
    @Id
    private Long contentId;

}
