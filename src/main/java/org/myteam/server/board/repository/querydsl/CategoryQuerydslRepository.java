package org.myteam.server.board.repository.querydsl;

import static org.myteam.server.global.exception.ErrorCode.RESOURCE_NOT_FOUND;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.myteam.server.board.domain.Category;
import org.myteam.server.board.domain.QCategory;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryQuerydslRepository implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Category> listSortedWithHierarchy() {
        QCategory parent = new QCategory("parent");
        QCategory child = new QCategory("child");

        return queryFactory.selectFrom(parent)
                .leftJoin(parent.children, child)
                .fetchJoin()
                .where(
                        parent.parent.isNull()
                )
                .orderBy(parent.orderIndex.asc(), child.orderIndex.asc())
                .fetch();
    }

    @Override
    public List<Category> listSortedRootCategories() {
        QCategory category = QCategory.category;

        return queryFactory.selectFrom(category)
                .where(category.parent.isNull()) // 부모가 없는 카테고리만 조회
                .orderBy(category.orderIndex.asc()) // 정렬
                .fetch();
    }

    @Override
    public Category getWithSortedChildrenById(Long id) {
        QCategory parent = QCategory.category;
        QCategory child = new QCategory("child");

        Category result = queryFactory.selectFrom(parent)
                .leftJoin(parent.children, child).fetchJoin() // 자식들과 함께 조회
                .where(parent.id.eq(id)) // 특정 ID에 해당하는 부모 조회
                .orderBy(child.orderIndex.asc()) // 자식 카테고리 정렬
                .fetchOne();

        if (result == null) {
            throw new PlayHiveException(RESOURCE_NOT_FOUND);
        }

        return result;
    }
}
