package org.myteam.server.admin.repository.simpleRepo;

import org.myteam.server.admin.entity.AdminChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;



public interface AdminChangeLogRepo extends JpaRepository<AdminChangeLog, Long> {
}
