package org.myteam.server.board.repository.querydsl;

import org.myteam.server.board.entity.Category;

import java.util.List;

public interface CategoryRepositoryCustom {
    List<Category> listSortedWithHierarchy();
    List<Category> listSortedRootCategories();
    Category getWithSortedChildrenById(Long id);
}