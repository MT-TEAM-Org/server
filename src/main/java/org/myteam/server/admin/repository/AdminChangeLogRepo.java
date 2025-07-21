package org.myteam.server.admin.repository;

import org.myteam.server.admin.entity.AdminChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminChangeLogRepo extends JpaRepository<AdminChangeLog,Long> {
}
