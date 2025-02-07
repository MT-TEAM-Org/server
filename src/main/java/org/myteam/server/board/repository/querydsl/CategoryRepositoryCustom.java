package org.myteam.server.board.repository.querydsl;

import java.util.List;
import org.myteam.server.board.domain.Category;

public interface CategoryRepositoryCustom {
    List<Category> listSortedWithHierarchy();

    List<Category> listSortedRootCategories();

    Category getWithSortedChildrenById(Long id);
}