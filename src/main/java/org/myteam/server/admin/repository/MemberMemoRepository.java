package org.myteam.server.admin.repository;


import io.swagger.v3.oas.models.OpenAPI;
import org.myteam.server.admin.entity.MemberMemo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberMemoRepository extends JpaRepository<MemberMemo,Long> {

    Optional<MemberMemo> findByReportedId(UUID uuid);
}
