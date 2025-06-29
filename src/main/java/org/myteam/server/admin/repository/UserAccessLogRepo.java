package org.myteam.server.admin.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.myteam.server.admin.entity.UserAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccessLogRepo extends JpaRepository<UserAccessLog,Long> {
}
