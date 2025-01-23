package org.myteam.server.board.repository;

import org.myteam.server.board.entity.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
    List<Category> findByParentIsNull(Sort sort);
    // List<Category> findByParentId(Long parentId);
    Optional<Category> findByParentIdAndOrderIndex(Long parentId, int orderIndex);
    Optional<Category> findById(long id);
    boolean existsByParentId(Long parentId);
}
