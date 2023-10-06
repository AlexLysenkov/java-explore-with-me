package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;

@Controller
@Validated
@Slf4j
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Получен POST запрос по эндпоинту '/admin/categories' на добавление category {}", newCategoryDto);
        return new ResponseEntity<>(categoryService.createCategory(newCategoryDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long catId,
                                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Получен PATCH запрос по эндпоинту '/admin/categories/{}' на обновление category с id {}", catId,
                catId);
        return ResponseEntity.ok(categoryService.updateCategory(catId, categoryDto));
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long catId) {
        log.info("Получен DELETE запрос по эндпоинту '/admin/categories/{}' на удаление user по id {}", catId, catId);
        categoryService.deleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
