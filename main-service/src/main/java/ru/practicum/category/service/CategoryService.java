package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long id, CategoryDto categoryDto);

    CategoryDto getCategoryById(Long id);

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    void deleteCategory(Long id);
}
