package org.myteam.server.global.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseTime {
    @CreatedDate
    @Column(name = "create_date", updatable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;




    //테스트시 createdate기준으로 회원가입한 멤버의 통계를 가져오는 코드가있는대 그를위해서 추가한것. colum도 updatable를 지워야 되긴하는대 우선적으론뻇음.
    public void updateCreateDateForTest(LocalDateTime time){

        this.createDate=time;

    }

}