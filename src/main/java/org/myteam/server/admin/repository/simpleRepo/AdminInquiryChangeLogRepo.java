package org.myteam.server.admin.repository.simpleRepo;

import org.myteam.server.admin.entity.AdminInquiryChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminInquiryChangeLogRepo extends JpaRepository<AdminInquiryChangeLog,Long> {
}
