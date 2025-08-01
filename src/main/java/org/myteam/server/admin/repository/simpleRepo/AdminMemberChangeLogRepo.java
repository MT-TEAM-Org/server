package org.myteam.server.admin.repository.simpleRepo;

import org.myteam.server.admin.entity.AdminMemberChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminMemberChangeLogRepo extends JpaRepository<AdminMemberChangeLog,Long> {
}
