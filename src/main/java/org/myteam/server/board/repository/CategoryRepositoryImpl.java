package org.myteam.server.board.repository;

import lombok.RequiredArgsConstructor;
import org.myteam.server.board.entity.Category;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.myteam.server.global.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public List<Category> listByParentIsNull() {
        return categoryJpaRepository.findByParentIsNull();
    }

    @Override
    public List<Category> listSortedByParentIsNull() {
        return categoryJpaRepository.findByParentIsNull(Sort.by(Sort.Direction.ASC, "orderIndex"));
    }

    /**
     * 부모 Id 파라미터 와 순번 항목 파라미터으로 존재하는 카테고리가 있는지 조회한다.
     * 부모 Id 가 Null 인 경우 최상단 카테고리 조회
     *
     * @param parentId 부모 Id
     * @param orderIndex 카테고리 순번
     * @return
     */
    @Override
    public Optional<Category> findByParentIdAndOrderIndex(Long parentId, int orderIndex) {
        return categoryJpaRepository.findByParentIdAndOrderIndex(parentId, orderIndex);
    }

    /**
     * 부모 Id 파라미터 와 순번 항목 파라미터으로 존재하는 카테고리가 있는지 조회한다.
     * 부모 Id 가 Null 인 경우 최상단 카테고리 조회
     *
     * @param parentId 부모 Id
     * @param orderIndex 카테고리 순번
     * @return
     */
    @Override
    public Category getByParentIdAndOrderIndex(Long parentId, int orderIndex) {
        return categoryJpaRepository.findByParentIdAndOrderIndex(parentId, orderIndex)
                .orElseThrow(() -> new PlayHiveException(RESOURCE_NOT_FOUND, "parentId -> " + parentId + ", orderIndex -> " + orderIndex + " 는 존재하지 않는 카테고리 입니다"));
    }

    @Override
    public Optional<Category> findById(long id) {
        return categoryJpaRepository.findById(id);
    }

    @Override
    public Category getById(long id) {
        return categoryJpaRepository.findById(id)
                .orElseThrow(() -> new PlayHiveException(RESOURCE_NOT_FOUND, "id -> " + id + " 는 존재하지 않는 카테고리 입니다"));
    }
}