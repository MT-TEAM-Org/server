package org.myteam.server.admin.repository.simpleRepo;

import org.myteam.server.admin.entity.AdminMemo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminMemoRepository extends JpaRepository<AdminMemo,Long> {
}
