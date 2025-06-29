package org.myteam.server.inquiry.repository;

import org.myteam.server.inquiry.domain.Inquiry;
import org.myteam.server.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Page<Inquiry> findByMember(Member member, Pageable pageable);


    @Modifying
    @Query("UPDATE Inquiry i SET i.isAdminAnswered=:checked where i.id=:id ")
    int updateById(Long id,boolean checked);


}
