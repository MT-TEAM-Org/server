package org.myteam.server.board.repository;

import java.util.List;
import java.util.Optional;
import org.myteam.server.board.domain.Category;

public interface CategoryRepository {
    List<Category> listByParentIsNull();

    List<Category> listSortedByParentIsNull();

    Optional<Category> findByParentIdAndOrderIndex(Long parentId, int orderIndex);

    Category getByParentIdAndOrderIndex(Long parentId, int orderIndex);

    Optional<Category> findById(long id);

    Category getById(long id);
}
