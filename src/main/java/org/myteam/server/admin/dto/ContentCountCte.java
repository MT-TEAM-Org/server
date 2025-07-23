package org.myteam.server.admin.dto;


import com.blazebit.persistence.CTE;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@CTE
public class ContentCountCte {
    @Id
    private Long contentId;

}
