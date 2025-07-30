package org.myteam.server.admin.repository.simpleRepo;

import org.myteam.server.admin.entity.AdminMemberMemo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminMemberMemoRepo extends JpaRepository<AdminMemberMemo,Long> {
}
