package org.myteam.server.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myteam.server.board.controller.reponse.CategoryResponse;
import org.myteam.server.board.dto.CategorySaveRequest;
import org.myteam.server.board.dto.CategoryUpdateRequest;
import org.myteam.server.board.entity.Category;
import org.myteam.server.board.repository.CategoryJpaRepository;
import org.myteam.server.board.repository.CategoryRepository;
import org.myteam.server.board.repository.querydsl.CategoryQuerydslRepository;
import org.myteam.server.global.exception.PlayHiveException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.myteam.server.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    private final CategoryJpaRepository categoryJpaRepository;

    private final CategoryQuerydslRepository categoryQuerydslRepository;

    @Transactional
    public CategoryResponse create(CategorySaveRequest categorySaveRequest) {
        Category categoryEntity = new Category(categorySaveRequest);

        if (categorySaveRequest.getParentId() != null) {
            Category parentCategory = categoryRepository.getById(categorySaveRequest.getParentId()); // 부모 카테고리 Entity 조회
            Integer depth = categorySaveRequest.getDepth();
            Integer parentDepth = parentCategory.getDepth();

            if (depth != (parentDepth + 1)) {
                log.warn("부모 카테고리와의 Depth 관계가 잘못 되었습니다.");
                throw new PlayHiveException(INVALID_PARAMETER);
            }

            categoryEntity.updateOrderIndex(
                    allocateNextOrderIndex(parentCategory) // calculateOrderIndex(parentCategory.getChildren())
            );
            categoryEntity.setParent(parentCategory); // 편의 메서드
        } else {
            // parentId 가 null 인 경우
            Integer depth = categorySaveRequest.getDepth();

            if (depth > 0) {
                log.warn("부모 카테고리 ID가 비어있습니다.");
                throw new PlayHiveException(INVALID_PARAMETER);
            }

            categoryEntity.updateOrderIndex(calculateOrderIndex(categoryJpaRepository.findByParentIsNull()));
        }

        Category savedEntity = categoryJpaRepository.save(categoryEntity);
        return new CategoryResponse(savedEntity);
    }

    @Transactional
    public CategoryResponse update(long id, CategoryUpdateRequest categoryUpdateRequest) {
        // 1. 기존 카테고리 조회
        Category category = categoryRepository.getById(id);

        // 2. 부모의 children 리스트 갯수로 categoryUpdateRequest orderIndex 값 수정 제한
        Category parent = category.getParent(); // 부모 카테고리 가져오기
        if (parent == null) {
            // 최상단 카테고리
            validateRootForReOrderIndex(categoryUpdateRequest);
        } else {
            // 그 외
            validateSubForReOrderIndex(parent, categoryUpdateRequest);
        }

        // 3. orderIndex 중복 확인
        Category targetCategory = categoryRepository.findByParentIdAndOrderIndex(
                category.getCategoryParentId(), // null 처리 되어 있음
                categoryUpdateRequest.getOrderIndex()
        ).orElse(null);

        if (targetCategory != null) {
            // 변경 순번(orderIndex)과 동일한 카테고리가 존재
            category.updateName(categoryUpdateRequest.getName());
            category.updateLink(categoryUpdateRequest.getLink());
            swapOrderIndex(category, targetCategory);
        } else {
            // 이름 및 순번 변경
            category.updateName(categoryUpdateRequest.getName());
            category.updateLink(categoryUpdateRequest.getLink());
            category.updateOrderIndex(categoryUpdateRequest.getOrderIndex());
        }

        // 4. 수정된 결과를 DTO로 변환 후 반환
        return new CategoryResponse(category);
    }

    private void swapOrderIndex(Category sourceCategory, Category targetCategory) {
        // orderIndex 교환용 변수
        int swapOrderIndex = targetCategory.getOrderIndex();
        targetCategory.updateOrderIndex(sourceCategory.getOrderIndex());
        sourceCategory.updateOrderIndex(swapOrderIndex);
    }

    @Transactional
    public CategoryResponse delete(long id) {
        // 1. 삭제할 엔티티를 조회
        Category category = categoryQuerydslRepository.getWithSortedChildrenById(id);

        // 연관관계 편의 메서드. 카테고리 순번 재정렬
        if (category.getParent() == null) {
            // 최상위 카테고리 순번 재정렬
            List<Category> topLevelCategories = categoryJpaRepository.findByParentIsNull(); // 최상위 카테고리 목록 조회

            topLevelCategories.remove(category); // 삭제 대상 제거
            reorderTopLevelCategories(topLevelCategories); // 카테고리의 orderIndex 재정렬
        } else {
            // 부모가 있는 경우 자식 순번 재정렬
            category.getParent().removeChild(category);
            reorderChildren(category.getParent());
        }

        // 2. 하위 카테고리 존재 여부 확인
        if (categoryJpaRepository.existsByParentId(id)) {
            log.debug("하위 카테고리 존재하여 삭제가 불가능 합니다. id : {}", id);
            throw new PlayHiveException(RESOURCE_CONFLICT);
        }

        // 3. DTO 생성 (삭제된 정보 반환)
        CategoryResponse response = new CategoryResponse(category);

        // 4. 삭제
        categoryJpaRepository.delete(category);

        // 5. 삭제된 DTO 반환
        return response;
    }

    public CategoryResponse getById(long id) {
        return categoryRepository.findById(id)
                .map(CategoryResponse::new) // Optional이 유효한 경우 CategoryResponse로 변환
                .orElseThrow(() -> new PlayHiveException(RESOURCE_NOT_FOUND)); // Optional이 비어 있으면 예외 발생
    }

    public CategoryResponse getWithSortedChildrenById(Long id) {
        return new CategoryResponse(categoryQuerydslRepository.getWithSortedChildrenById(id));
    }

    public List<CategoryResponse> getRootCategories() {
        final List<Category> all = categoryQuerydslRepository.listSortedRootCategories();
        return all.stream().map(CategoryResponse::fromWithoutChildren).collect(Collectors.toList());
    }

    public List<CategoryResponse> getAllCategories() {
        final List<Category> all = categoryQuerydslRepository.listSortedWithHierarchy();
        return all.stream().map(CategoryResponse::new).collect(Collectors.toList());
    }

    /**
     * 최상단 카테고리 카테고리 변경에 대한 유효성 검증
     *
     * @param categoryUpdateRequest 카테고리 변경 정보
     * @throws PlayHiveException
     */
    public void validateRootForReOrderIndex(CategoryUpdateRequest categoryUpdateRequest) throws PlayHiveException {
        List<Category> rootCategories = categoryJpaRepository.findByParentIsNull(); // 최상단 카테고리 목록
        int rootCategoriesCount = rootCategories.size(); // 최상단 카테고리 개수
        Integer orderIndex = categoryUpdateRequest.getOrderIndex();

        // orderIndex가 최상단 카테고리 수보다 많거나 같으면 예외 발생
        if (orderIndex > rootCategoriesCount) {
            log.error("orderIndex는 최상단 카테고리의 수보다 작아야 합니다.");
            throw new PlayHiveException(INVALID_PARAMETER);
        }
    }

    /**
     * 최상단 카테고리가 아닌 그 외 카테고리 변경에 대한 유효성 검증
     *
     * @param parent 부모 카테고리
     * @param categoryUpdateRequest 카테고리 변경 정보
     * @throws PlayHiveException
     */
    public void validateSubForReOrderIndex(Category parent, CategoryUpdateRequest categoryUpdateRequest) throws PlayHiveException {
        // 그 외
        List<Category> siblings = parent.getChildren(); // 부모의 자식 리스트
        int siblingsCount = siblings.size(); // 형제 카테고리(자식)의 개수
        Integer orderIndex = categoryUpdateRequest.getOrderIndex();

        // orderIndex가 형제 카테고리 수보다 많거나 같으면 예외 발생
        if (orderIndex > siblingsCount) {
            log.error("orderIndex는 부모 카테고리의 자식 수보다 작아야 합니다.");
            throw new PlayHiveException(INVALID_PARAMETER);
        }
    }

    /**
     * 최상위 카테고리 순번 재정렬
     *
     * @param topLevelCategories 최상위 카테고리 목록
     */
    public void reorderTopLevelCategories(List<Category> topLevelCategories) {
        for (int i = 0; i < topLevelCategories.size(); i++) {
            topLevelCategories.get(i).updateOrderIndex(i + 1);
        }
    }

    /**
     * 카테고리 순번을 설정
     * @param parent 부모 카테고리
     */
    public int allocateNextOrderIndex(Category parent) {
        return calculateOrderIndex(parent.getChildren());
    }

    /**
     * 카테고리 목록의 갯수로 부터 다음 순번을 계산
     *
     * @param list 카테고리 목록
     * @return 다음 카테고리 순번
     */
    private int calculateOrderIndex(List<?> list) {
        return (list == null) ? 1 : list.size() + 1;
    }

    /**
     * 카테고리 순번을 사이즈에 맞게 재정렬한다.
     *
     * @param category 카테고리
     */
    private void reorderChildren(Category category) {
        for (int i = 0; i < category.getChildren().size(); i++) {
            category.getChildren().get(i).updateOrderIndex(i + 1);
        }
    }
}