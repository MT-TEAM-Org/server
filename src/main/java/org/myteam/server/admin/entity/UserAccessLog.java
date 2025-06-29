package org.myteam.server.admin.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.myteam.server.global.domain.Base;
import org.myteam.server.global.domain.BaseTime;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAccessLog extends BaseTime {


    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false,updatable = false,unique = true)
    private UUID publicId;




    @Builder
    public UserAccessLog(UUID id){
        this.publicId=id;


    }
}
