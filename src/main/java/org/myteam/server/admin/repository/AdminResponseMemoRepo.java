package org.myteam.server.admin.repository;

import org.myteam.server.admin.entity.AdminResponseMemo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface AdminResponseMemoRepo extends JpaRepository<AdminResponseMemo,Long> {


    Optional<AdminResponseMemo> findByWriterId(UUID memberid);


    Optional<AdminResponseMemo> findByInquiryId(Long inquiryId);
}
