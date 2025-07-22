package org.myteam.server.member.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAccess {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID publicId;

    private LocalDateTime accessTime;

    private String ip;

    @Builder
    public MemberAccess(UUID publicId,LocalDateTime accessTime,String ip){
        this.accessTime=accessTime;
        this.publicId=publicId;
        this.ip=ip;
    }

    public void updateMemberAccessIp(String ip){
        this.ip=ip;
    }

}
