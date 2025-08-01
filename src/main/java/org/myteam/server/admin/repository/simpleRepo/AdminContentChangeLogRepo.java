package org.myteam.server.admin.repository.simpleRepo;

import org.myteam.server.admin.entity.AdminContentChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminContentChangeLogRepo extends JpaRepository<AdminContentChangeLog,Long> {

}
