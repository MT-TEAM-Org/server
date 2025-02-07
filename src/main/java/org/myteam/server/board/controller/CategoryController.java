package org.myteam.server.board.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.myteam.server.board.controller.reponse.CategoryResponse;
import org.myteam.server.board.dto.CategorySaveRequest;
import org.myteam.server.board.dto.CategoryUpdateRequest;
import org.myteam.server.board.service.CategoryService;
import org.myteam.server.global.web.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.myteam.server.global.web.response.ResponseStatus.SUCCESS;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // CREATE: 카테고리 생성
    @PostMapping
    public ResponseEntity<ResponseDto<CategoryResponse>> createCategory(@Valid @RequestBody CategorySaveRequest categorySaveRequest) {
        CategoryResponse response = categoryService.create(categorySaveRequest);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "카테고리 생성 성공", response));
    }

    // UPDATE: 카테고리 수정
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<CategoryResponse>> updateCategory(@PathVariable Long id,
                                                                        @Valid @RequestBody CategoryUpdateRequest categoryUpdateRequest) {
        CategoryResponse response = categoryService.update(id, categoryUpdateRequest);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "카테고리 수정 성공", response));
    }

    // DELETE: 카테고리 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "카테고리 삭제 성공", null));
    }

    // READ: 단일 카테고리 조회
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        CategoryResponse response = categoryService.getById(id);
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "카테고리 조회 성공", response));
    }

    @GetMapping()
    public ResponseEntity<ResponseDto<List<CategoryResponse>>> getAllCategories() {
        final List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "카테고리 목록 조회 성공", categories));
    }

    @GetMapping("/roots")
    public ResponseEntity<ResponseDto<List<CategoryResponse>>> getRootCategories() {
        List<CategoryResponse> rootCategories = categoryService.getRootCategories();
        return ResponseEntity.ok(new ResponseDto<>(SUCCESS.name(), "루트 카테고리 조회 성공", rootCategories));
    }
}