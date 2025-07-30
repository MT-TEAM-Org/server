package org.myteam.server.admin.repository.simpleRepo;

import org.myteam.server.admin.entity.AdminContentMemo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminContentMemoRepo extends JpaRepository<AdminContentMemo,Long> {
}
