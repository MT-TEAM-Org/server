package org.myteam.server.board.controller.reponse;

import lombok.Getter;
import org.myteam.server.board.entity.Category;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CategoryResponse {
    private Long id;
    private String name;
    private Integer orderIndex;
    private Integer depth;
    private Long parentId;
    private List<CategoryResponse> children;
    private String link;

    public CategoryResponse(Long id, String name, Integer orderIndex, Integer depth, Long parentId, List<CategoryResponse> children, String link) {
        this.id = id;
        this.name = name;
        this.orderIndex = orderIndex;
        this.depth = depth;
        this.parentId = parentId;
        this.children = children;
        this.link = link;
    }

    public CategoryResponse(final Category categoryEntity) {
        this.id = categoryEntity.getId();
        this.name = categoryEntity.getName();
        this.depth = categoryEntity.getDepth();
        this.orderIndex = categoryEntity.getOrderIndex();
        this.parentId = categoryEntity.getParent() != null ? categoryEntity.getParent().getId() : null; // null 체크
        this.children = categoryEntity.getChildren().stream().map(CategoryResponse::new).collect(Collectors.toList());
        this.link = categoryEntity.getLink();
    }

    public static CategoryResponse fromWithoutChildren(Category entity) {
        if (entity == null) return null;

        return new CategoryResponse(
                entity.getId(),
                entity.getName(),
                entity.getOrderIndex(),
                entity.getDepth(),
                entity.getCategoryParentId(),
                null, // children 제외
                entity.getLink()
        );
    }
}