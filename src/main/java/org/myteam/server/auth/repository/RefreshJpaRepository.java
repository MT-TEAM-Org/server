package org.myteam.server.auth.repository;

import org.myteam.server.auth.entity.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface RefreshJpaRepository extends JpaRepository<Refresh, Long> {
    Boolean existsByRefreshAndPublicId(String refresh, UUID publicId);

    @Transactional
    void deleteByPublicId(UUID publicId);
}
