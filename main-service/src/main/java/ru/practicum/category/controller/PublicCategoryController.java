package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@Validated
@Slf4j
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long catId) {
        log.info("Получен GET запрос по эндпоинту '/categories/{}' на получение category по id {}", catId, catId);
        return ResponseEntity.ok(categoryService.getCategoryById(catId));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories(@RequestParam(required = false, defaultValue = "0")
                                                              @PositiveOrZero Integer from,
                                                              @RequestParam(required = false, defaultValue = "10")
                                                              @Positive Integer size) {
        log.info("Получен GET запрос по эндпоинту '/categories' на получение categories с параметрами from = {}, " +
                "size = {}", from, size);
        return ResponseEntity.ok(categoryService.getAllCategories(from, size));
    }
}
