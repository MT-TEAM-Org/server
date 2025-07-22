package org.myteam.server.admin.dto;


import com.blazebit.persistence.CTE;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@CTE
@Getter
@Entity
public class MemberContentCountCte {
    @Id
    private Long contentId;
    private Long count;
}
